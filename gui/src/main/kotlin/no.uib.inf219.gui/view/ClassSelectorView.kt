package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.loader.DynamicClassLoader
import tornadofx.*


/**
 * @author Elg
 */
class ClassSelectorView : View("Select implementation") {

    private val searchResult: ObservableList<Class<*>> = ArrayList<Class<Any>>().asObservable()

    private var filteredData = FilteredList(searchResult)

    var result: Class<*>? = null

    private val searchingProperty = SimpleBooleanProperty()
    private var searching by searchingProperty

    override val root = vbox {
        //TODO search filtering

        val label = label(SEARCHING) {
            searchingProperty.onChange {
                runLater {
                    text = if (searching) SEARCHING else NO_SUBCLASSES_FOUND
                }
            }
        }


        val resultList = vbox {
            addClass(Styles.parent)

            textfield {
                promptText = "Full class name"
                textProperty().onChange {
                    println("a")
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

                onUserSelect {
                    result = it
                }

                //close when pressing enter and something is selected or double clicking
                onUserSelect(2) {
                    result = it
                    close()
                }
                addEventFilter(KeyEvent.ANY) { event ->
                    if (event.code == KeyCode.ENTER && result != null) {
                        close()
                    }
                }
            }
        }

        //Make sure it looks Nice
        searchResult.onChange {
            runLater {
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


    fun searchForSubtypes(superType: JavaType) {

        require(superType.rawClass != null) { "Given java '$superType' types does not have a raw class" }
        require(!superType.isPrimitive) { "Given class '${superType.toCanonical()}' cannot be primitive" }
        require(!superType.isFinal) { "Given class '${superType.toCanonical()}' cannot be final" }
        require(superType.rawClass.canonicalName != null) { "Given class '${superType.toCanonical()}' must have a canonical name" }


        synchronized(this) {

            searching = true
            val superClass: Class<*> = superType.rawClass

            ClassGraph()
//                .verbose() // Log to stderr
                .enableExternalClasses()
                .addClassLoaders(DynamicClassLoader.getClassLoaders())
                .scan().use { scanResult ->
                    val classes: ClassInfoList = if (superClass.isInterface) {
                        scanResult.getClassesImplementing(superClass.name)
                    } else {
                        scanResult.getSubclasses(superClass.canonicalName)
                    }
                    searchResult.clear()
                    for (clazz in classes.loadClasses()) {
                        searchResult.add(clazz as Class<*>)
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

