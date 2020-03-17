package no.uib.inf219.gui.view

import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import no.uib.inf219.extra.Persistent
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.exceptions.MissingPropertyException
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView.mapper
import tornadofx.*
import java.io.File


/**
 * The view of the main editor
 *
 * @author Elg
 */
class ObjectEditor : View() {

    val controller: ObjectEditorController by param()
    private var lastFile: File? by Persistent()

    private fun createPropEditor(): BorderPane {
        val editor: PropertyEditor = find("controller" to controller)
        editor.root.center = controller.rootCb.toView(this, controller)
        return editor.root
    }

    override val root = borderpane() {

        center = splitpane {
            addClass(Styles.parent)
            setDividerPositions(0.25)

            this += NodeExplorerView(controller).root
            this += createPropEditor()
//            openInternalBuilderWindow("test", owner = FX.primaryStage.borderpane()) {
//
//            }
        }

        bottom = hbox {
            addClass(Styles.parent)

            button("Save") {
                setOnAction {

                    val obj = toJson()
                    if (obj == null) {
                        OutputArea.logln("Cannot save object as it is not valid. Please validate it first")
                        return@setOnAction
                    }

                    val files = chooseFile(
                        "Choose where to save",
                        arrayOf(
                            FileChooser.ExtensionFilter(
                                "JSON",
                                "*.json"
                            ), FileChooser.ExtensionFilter(
                                "YAML",
                                "*.yaml",
                                "*.yml"
                            ), FileChooser.ExtensionFilter(
                                "Properties",
                                "*.properties"
                            ),
                            FileChooser.ExtensionFilter(
                                "XML",
                                "*.xml"
                            ),
                            FileChooser.ExtensionFilter(
                                "Comma Separated Values",
                                "*.csv"
                            ),
                            FileChooser.ExtensionFilter(
                                "Plain text",
                                "*.txt"
                            ),
                            FileChooser.ExtensionFilter("All files", "*")
                        ),
                        lastFile,
                        FileChooserMode.Save
                    )

                    if (files.isEmpty()) return@setOnAction
                    val file = files[0]

                    ControlPanelView.runAsync {
                        mapper.writeValue(file, mapper.convertValue(controller.rootCb, controller.rootCb.type))
                        OutputArea.logln("Saved object to file ${file.canonicalPath}")
                    }
                }
            }

            button("Validate") {
                setOnAction {
                    OutputArea.logln("Validating...")
                    runAsync {

                        val obj = toJson()
                        if (obj != null) {
                            OutputArea.logln("Successfully created object!")
                            OutputArea.logln("json=$obj")
                        }
                    }
                }
            }
            this += OutputArea.clearButton()
        }
    }

    private fun toJson(): String? {
        try {
            return if (ControlPanelView.unsafeSerialization.value) {
                mapper.writeValueAsString(controller.rootCb)
            } else {
                val obj = mapper.convertValue<Any>(controller.rootCb, controller.rootCb.type)!!
                mapper.writeValueAsString(obj)
            }
        } catch (e: MissingPropertyException) {
            OutputArea.logln("Failed to create object.\n$e")
        } catch (e: Throwable) {
            //As we load classes from external jars, we do not know what class loader the created object will be in
            //We can only use one type factory at once, maybe find a way to do this better?

            OutputArea.logln("Failed to create object due to an exception. Maybe you tried to create an object which require a non-null parameter is null.")
            OutputArea.logln("${e.javaClass.simpleName}: ${e.message}")
            e.printStackTrace()
        }
        return null
    }
}
