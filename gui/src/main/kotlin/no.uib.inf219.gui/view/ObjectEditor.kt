package no.uib.inf219.gui.view

import javafx.stage.FileChooser
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView.mapper
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

            button("Save") {
                setOnAction {

                    val obj = toObject()
                    if (obj == null) {
                        OutputArea.logln("Cannot save object as it is not valid. Please validate it first")
                        return@setOnAction
                    }

                    val files = chooseFile(
                        "Choose where to save",
                        arrayOf(
                            FileChooser.ExtensionFilter(
                                "Common types",
                                "*.json",
                                "*.yaml",
                                "*.yml",
                                "*.txt",
                                "*.csv",
                                "*.properties",
                                "*.xml"
                            ),
                            FileChooser.ExtensionFilter("All files", "*")
                        ),
                        FileChooserMode.Save
                    )

                    if (files.isEmpty()) return@setOnAction
                    val file = files[0]

                    ControlPanelView.runAsync {
                        mapper.writeValue(file, obj)
                        OutputArea.logln("Saved object to file ${file.canonicalPath}")
                    }
                }
            }

            button("Validate") {
                setOnAction {
                    OutputArea.logln("Validating...")
                    runAsync {

                        val obj = toObject()
                        if (obj == null) {
                            OutputArea.logln("Object created without error, but it is null")
                        } else {
                            OutputArea.logln("Successfully created object!")
                            OutputArea.logln("obj=$obj")
                            OutputArea.logln("json=${mapper.writeValueAsString(obj)}")
                        }

                    }
                }
            }
            this += OutputArea.clearButton()
        }
    }

    fun toObject(): Any? {
        try {
            return controller.rootBuilder.toObject()!!
        } catch (e: Throwable) {
            OutputArea.logln("Failed to create object due to")
            OutputArea.logln(e.toString())
        }
        return null
    }
}

