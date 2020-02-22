package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import no.uib.inf219.extra.addClassLoaders
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.ems
import no.uib.inf219.gui.loader.DynamicClassLoader
import tornadofx.*


/**
 * @author Elg
 */
class ClassSelectorView : View("Select implementation") {

    private val searchResult: ObservableList<Class<*>> = ArrayList<Class<Any>>().asObservable()
    private val filteredData = FilteredList(searchResult)

    var result: Class<*>? = null

    private val searchingProperty = SimpleBooleanProperty()
    private var searching by searchingProperty

    private val label: Node
    private val resultList: Node

    override val root = borderpane()

    init {
        with(root) {

            style {
                minWidth = 45.ems
                minHeight = 25.ems
            }

            label = hbox {
                alignment = Pos.CENTER
                text(SEARCHING) {
                    addClass(Styles.largefont)

                    searchingProperty.onChange {
                        runLater {
                            text = if (searching) SEARCHING else NO_SUBCLASSES_FOUND
                        }
                    }
                }
            }

            resultList = vbox {

                addClass(Styles.parent)

                textfield {
                    promptText = "Full class name"
                    textProperty().onChange {
                        if (text.isNullOrBlank()) {
                            filteredData.predicate = null
                        } else {
                            filteredData.setPredicate {
                                it.canonicalName.contains(text, ignoreCase = true)
                            }
                        }
                    }
                }

                listview(filteredData) {

                    onUserSelect {
                        result = it
                    }

                    //close when pressing enter and something is selected or double clicking
                    onUserSelect(2) {
                        result = it
                        cleanAndClose()
                    }

                    addEventHandler(KeyEvent.ANY) { event ->
                        if (event.code == KeyCode.ENTER && result != null) {
                            cleanAndClose()
                        }
                    }

                    cellFormat {
                        text = it.canonicalName
                    }
                }
            }

            searchingProperty.onChange {
                runLater {
                    if (searching) {
                        center.replaceWith(label, sizeToScene = true, centerOnScreen = true)
                    } else {
                        center.replaceWith(resultList, sizeToScene = true, centerOnScreen = true)
                    }
                }
            }
            center = label
        }
    }

    private fun clean() {
        searching = true
        searchResult.clear()
        root.center.replaceWith(label, centerOnScreen = true)
    }

    private fun cleanAndClose() {
        clean()
        close()
    }

    fun searchForSubtypes(superType: JavaType) {

        require(superType.rawClass != null) { "Given java '$superType' types does not have a raw class" }
        require(!superType.isPrimitive) { "Given class '${superType.toCanonical()}' cannot be primitive" }
        require(!superType.isFinal) { "Given class '${superType.toCanonical()}' cannot be final" }
        require(superType.rawClass.canonicalName != null) { "Given class '${superType.toCanonical()}' must have a canonical name" }


        synchronized(this) {
            searching = true
            val superClass: Class<*> = superType.rawClass

            ClassGraph()
                .enableExternalClasses()
                .addClassLoaders(DynamicClassLoader.getClassLoaders())
                .scan().use { scanResult ->
                    val cil: ClassInfoList =
                        when {
                            superClass == Any::class.java -> scanResult.allStandardClasses
                            superClass.isInterface -> scanResult.getClassesImplementing(superClass.name)
                            else -> scanResult.getSubclasses(superClass.canonicalName)
                        }
                    val classes = cil.loadClasses(true).filter {
                        try {
                            it.canonicalName != null
                        } catch (e: Throwable) {
                            false
                        }
                    }
                    runLater {
                        searchResult.setAll(classes)
                    }
                }
            searching = false
        }
    }

    companion object {
        const val TITLE_TEXT = "Select implementation of"
        const val SEARCHING = "Searching..."
        const val NO_SUBCLASSES_FOUND = "No subclasses found"
    }
}

