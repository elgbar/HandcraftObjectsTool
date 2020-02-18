package no.uib.inf219.gui.view

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


    override val root = borderpane() {

        center = splitpane {
            addClass(Styles.parent)
            setDividerPositions(0.25)

            this += NodeExplorerView(controller).root

            val editor = PropertyEditor(controller)
            editor.root.center = controller.rootBuilder.toView(this, controller)

            this += editor.root

        }

        bottom = hbox {
            addClass(Styles.parent)

            button("Validate") {
                setOnAction {
                    OutputArea.logln("Validating...")
                    runAsync {
                        try {
                            val obj = controller.rootBuilder.toObject()
                            ui {
                                if (obj == null) {
                                    OutputArea.logln("Object created without error, but it is null")
                                } else {
                                    OutputArea.logln("Successfully created object!")
                                    OutputArea.logln("obj=$obj")
                                    OutputArea.logln("json=${ControlPanelView.mapper.writeValueAsString(obj)}")
                                }
                            }
                        } catch (e: Throwable) {
                            ui {
                                OutputArea.logln("Failed to create object due to")
                                OutputArea.logln(e.toString())
                            }
                        }
                    }
                }
            }

            this += OutputArea.clearButton()
        }
    }
}

