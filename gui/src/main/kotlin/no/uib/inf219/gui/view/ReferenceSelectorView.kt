package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ReferenceClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.ems
import tornadofx.*

/**
 * @author Elg
 */
class ReferenceSelectorView : View("Reference") {

    private val searchResult: ObservableList<ClassBuilder<*>> = ArrayList<ClassBuilder<*>>().asObservable()
    private val filteredData = FilteredList(searchResult)

    private var result: ClassBuilder<*>? = null

    val controller: ObjectEditorController by param()

    private val searchingProperty = SimpleBooleanProperty()
    private var searching by searchingProperty
    private val label: Node
    private lateinit var textLabelProperty: StringProperty
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
                text(ClassSelectorView.SEARCHING) {
                    addClass(Styles.largefont)

                    searchingProperty.onChange {
                        runLater {
                            text = if (searching) ClassSelectorView.SEARCHING else ClassSelectorView.NO_SUBCLASSES_FOUND
                        }
                    }
                }
            }

            resultList = vbox {

                addClass(Styles.parent)

                textfield {
                    promptText = "Class builder name"
                    textLabelProperty = textProperty()
                    textProperty().onChange {
                        if (text.isNullOrBlank()) {
                            filteredData.predicate = null
                        } else {
                            filteredData.setPredicate {
                                it.getPreviewValue().contains(text, ignoreCase = true)
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
                        close()
                    }

                    cellFormat() {
                        text = it.getPreviewValue()
                    }

                    addEventHandler(KeyEvent.ANY) { event ->
                        if (event.code == KeyCode.ENTER && result != null) {
                            close()
                        }
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


    fun createReference(
        type: JavaType,
        key: ClassBuilder<*>,
        parent: ClassBuilder<*>
    ): ReferenceClassBuilder? {
        tornadofx.runAsync {
            searching = true
            searchResult.setAll(
                findInstancesOf(type, controller.findRootController().rootCb).filter { it != parent.getChild(key) })
            searching = false
        }
        openModal(block = true)

        val ref = result ?: return null
        val refKey = ref.key ?: return null
        val refParent = ref.parent ?: return null
        return ReferenceClassBuilder(refKey, refParent, key, parent)
    }

    companion object {

        internal fun findInstancesOf(
            type: JavaType,
            cb: ClassBuilder<*>
        ): Set<ClassBuilder<*>> {

            //the set to hold all children of this class builder. Use set to prevent duplicates
            val allChildren = HashSet<ClassBuilder<*>>()
            allChildren.add(cb) //remember to also add the parent
            for (child in cb.getChildren()) {
                allChildren.addAll(findInstancesOf(type, child))
            }

            //find all children that is the correct type
            // and isn't a ReferenceClassBuilder to prevent cycles
            return allChildren.filter { it.type == type && it !is ReferenceClassBuilder }.toSet()
        }
    }
}
