package no.uib.inf219.gui.view

import javafx.stage.FileChooser
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.gui.view.ControlPanelView.printStackTraceOnSerError
import no.uib.inf219.gui.view.ControlPanelView.unsafeSerialization
import tornadofx.*


/**
 * The view of the main editor
 *
 * @author Elg
 */
class ObjectEditorBackgroundView : View("Object Editor Background") {

    val controller: ObjectEditorController by param()

    override val root = borderpane() {

        center = splitpane {
            addClass(Styles.parent)
            setDividerPositions(0.25)

            this += NodeExplorerView(controller).root.also {
                runLater { it.requestFocus() }
            }
            this += find<PropertyEditor>("controller" to controller).root
            controller.select(controller.root)

//            openInternalBuilderWindow("test", owner = FX.primaryStage.borderpane()) {
//            }
        }

        bottom = hbox {
            addClass(Styles.parent)

            button("Save").action { save() }
            button("Validate").action { validate() }
            this += OutputArea.clearButton()
        }
    }

    private fun toJson(): String? {
        try {
            return if (unsafeSerialization) {
                mapper.writeValueAsString(controller.root)
            } else {
                val obj = mapper.convertValue<Any>(controller.root, controller.root.type)!!
                mapper.writeValueAsString(obj)
            }
        } catch (e: Throwable) {
            //As we load classes from external jars, we do not know what class loader the created object will be in
            //We can only use one type factory at once, maybe find a way to do this better?

            OutputArea.logln("Failed to create object due to an exception. Maybe you tried to create an object which require a non-null parameter is null.")
            OutputArea.logln("${e.javaClass.simpleName}: ${e.message}")
            if (printStackTraceOnSerError) {
                e.printStackTrace()
                OutputArea.logln(e)
            }
        }
        return null
    }

    fun save() {
        val obj = toJson()
        if (obj == null) {
            warning(
                "Failed to save ${controller.root}",
                "Cannot save object as it is not valid. Please validate it first",
                owner = currentWindow
            )
            return
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
            Settings.lastFolderSaved,
            FileChooserMode.Save
        ) {
            initialFileName = controller.root.type.rawClass.simpleName
        }

        if (files.isEmpty()) return
        val file = files[0]
        Settings.lastFolderSaved = file.parentFile

        ControlPanelView.runAsync {
            file.writeText(obj)
            OutputArea.logln("Saved object to file ${file.canonicalPath}")
        }
    }

    fun validate() {
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
