package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.Duration
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

    override val root = vbox {

        val label = label(SEARCHING) {
            addClass(Styles.headLineLabel)

            searchingProperty.onChange {
                runLater {
                    text = if (searching) SEARCHING else NO_SUBCLASSES_FOUND
                }
            }
        }


        val resultList = vbox {
            hide() //initially hide
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

                fitToParentSize()
                style {
                    minWidth = 45.ems
                    minHeight = 25.ems
                }

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

        searchResult.onChange {
            runLater(Duration.millis(10.0)) {
                if (searchResult.isEmpty()) {
                    resultList.hide()
                    label.show()
                } else {
                    resultList.show()
                    label.hide()
                }
                scene.window.sizeToScene()
            }
        }
    }

    private fun clean() {
        searching = true
        searchResult.clear()
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

            clean()
            val superClass: Class<*> = superType.rawClass

            ClassGraph()
//                .verbose() // Log to stderr
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
                        searchResult.addAll(classes)
                    }
                }
            searching = false
        }
    }

    fun ClassGraph.addClassLoaders(cls: Collection<ClassLoader>): ClassGraph {
        for (it in cls) {
            addClassLoader(it)
        }
        return this
    }

    companion object {
        const val TITLE_TEXT = "Select implementation of"
        const val SEARCHING = "Searching..."
        const val NO_SUBCLASSES_FOUND = "No subclasses found"
    }
}

