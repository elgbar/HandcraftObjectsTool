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
import no.uib.inf219.extra.type
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.ems
import no.uib.inf219.gui.loader.DynamicClassLoader
import tornadofx.*
import java.lang.reflect.Modifier


/**
 * A view to allow for selection of classes via a GUI. It is near identical to how this works in IntelliJ IDEA.
 *
 * The opened GUI will use [ClassGraph] to find all subclasses (but no interfaces) of
 *
 * ## Example usage
 *
 * ```kotlin
 * val subclass = tornadofx.find<ClassSelectorView>().subtypeOf(Any::class.type())
 * ```
 *
 * @author Elg
 */
class ClassSelectorView : View("Select implementation") {

    private val searchResult: ObservableList<String> = ArrayList<String>().asObservable()
    private val filteredData = FilteredList(searchResult)

    /**
     * The class selected by the use when closing the dialog
     */
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
                                it.contains(text, ignoreCase = true)
                            }
                        }
                    }
                }

                listview(filteredData) {

                    onUserSelect {
                        result = DynamicClassLoader.classFromName(it)
                    }

                    //close when pressing enter and something is selected or double clicking
                    onUserSelect(2) {
                        result = DynamicClassLoader.classFromName(it)
                        close()
                    }

                    addEventHandler(KeyEvent.ANY) { event ->
                        if (event.code == KeyCode.ENTER && result != null) {
                            close()
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

    /**
     * Ask the user to select a subclass of the given [superType]. This method will block till the class is selected or the user cancels the search (in which case the returned value will be `null`)
     *
     * @param superType The super class to find the subclasses to
     * @param showAbstract If abstract classes should be listed
     *
     * @return A user selected subtype of [superType]. The returned type is guaranteed to not be abstract if [showAbstract] is `false`. `null` might be returned if the user cancels the search (it closing the selection window)
     *
     * @throws IllegalArgumentException if the super type is not allowed. A type is not allowed if it is a primitive class, final class or does not have a canonical name (such as local and anonymous classes)
     * @throws IllegalArgumentException if the given types does not have a java class associated with it
     */
    fun subtypeOf(superType: JavaType, showAbstract: Boolean = false): JavaType? {
        tornadofx.runAsync {
            searchForSubtypes(superType, showAbstract)
        }
        openModal(block = true)
        return result?.type()
    }

    /**
     * Update the list of subclasses to be those of [superType]. Unless doing something fancy it's recommended to use [subtypeOf]
     *
     * @param superType The super class to find the subclasses to
     * @param showAbstract If abstract classes should be listed
     */
    fun searchForSubtypes(superType: JavaType, showAbstract: Boolean) {

        require(superType.rawClass != null) { "Given java '$superType' types does not have a raw class" }
        require(superType.rawClass.canonicalName != null) { "Given class '${superType.toCanonical()}' must have a canonical name" }
        require(!superType.isPrimitive) { "Given class '${superType.toCanonical()}' cannot be primitive" }
        require(!superType.isFinal) { "Given class '${superType.toCanonical()}' cannot be final" }

        synchronized(this) {

            searching = true
            result = null
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
                    val classes = cil.filter {
                        (showAbstract || !Modifier.isAbstract(it.modifiers))
                    }.names

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

