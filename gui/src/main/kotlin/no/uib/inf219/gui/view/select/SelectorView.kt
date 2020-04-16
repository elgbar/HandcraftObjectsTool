package no.uib.inf219.gui.view.select

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.VBox
import no.uib.inf219.extra.onChange
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.ems
import tornadofx.*

/**
 * @author Elg
 */
abstract class SelectorView<T>(title: String) : View(title) {

    protected val searchResult: ObservableList<T> = ArrayList<T>().asObservable()
    protected val filteredData = FilteredList(searchResult)

    /**
     * The [T] selected by the use when closing the dialog
     */
    protected var result: T? = null

    val controller: ObjectEditorController by param()

    protected val searchingProperty = SimpleBooleanProperty()
    protected var searching by searchingProperty
    protected val label: Node
    protected lateinit var textLabelProperty: StringProperty
    protected val resultList: VBox

    final override val root = borderpane()

    abstract fun cellText(elem: T): String
    abstract val promptText: String
    abstract fun confirmAndClose()

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
                    promptText = this@SelectorView.promptText
                    textLabelProperty = textProperty()
                    textProperty().onChange {
                        if (text.isNullOrBlank()) {
                            filteredData.predicate = null
                        } else {
                            filteredData.setPredicate {
                                cellText(it).contains(text, ignoreCase = true)
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
                        confirmAndClose()
                    }

                    cellFormat() {
                        text = cellText(it)
                    }

                    addEventHandler(KeyEvent.ANY) { event ->
                        if (event.code == KeyCode.ENTER && result != null) {
                            confirmAndClose()
                        } else if (event.code == KeyCode.ESCAPE) {
                            result = null
                            close()
                        }
                    }
                }
            }

            searchingProperty.onChange {
                runLater {
                    if (searching) {
                        center.replaceWith(label, sizeToScene = true, centerOnScreen = true)
                        this@SelectorView.title = "Searching..."
                    } else {
                        center.replaceWith(resultList, sizeToScene = true, centerOnScreen = true)
                        this@SelectorView.title = promptText
                    }
                }
            }
            center = label
        }
    }
}
