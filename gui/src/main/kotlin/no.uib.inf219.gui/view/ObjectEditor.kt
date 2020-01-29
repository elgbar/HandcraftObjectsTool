package no.uib.inf219.gui.view

import javafx.scene.control.TextArea
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * The view of the main editor
 *
 *
 *
 * @param clazz The class we are editing
 * @author Elg
 */
class ObjectEditor(private val controller: ObjectEditorController) : View() {


    override val root = borderpane {


        left = NodeExplorerView(controller).root
        center = PropertyEditor(controller).root
        bottom = vbox {
            val buttons = hbox {
                addClass(Styles.parent)
            }

            val output: TextArea = scrollpane(fitToHeight = true, fitToWidth = true).textarea() {
                editableProperty().set(false)
            }
            buttons += button("Validate") {
                setOnAction {
                    output.appendText("Validating...\n")
                    runAsync {
                        try {
                            val obj = controller.rootBuilder.toObject()
                            ui {
                                if (obj == null) {
                                    output.appendText("Object created without error, but it is null")
                                } else
                                    output.appendText(
                                        "Successfully created object!\nobj=$obj\nAs json:\n" +
                                                SerializationManager.dump(obj)
                                    )
                            }
                        } catch (e: Throwable) {
                            ui {
                                output.appendText("Failed to create object due to \n$e\n")
                            }
                        }
                    }
                }
            }
        }
    }
}
