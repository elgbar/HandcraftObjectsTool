package no.uib.inf219.gui.view

import javafx.scene.control.ButtonType.NO
import javafx.scene.control.ButtonType.YES
import javafx.stage.FileChooser
import no.uib.inf219.extra.NO_DISABLE_WARNING
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Settings.unsafeSerialization
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.gui.view.ControlPanelView.mrBeanModule
import no.uib.inf219.gui.view.LoggerView.log
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
    }

    private fun toJson(): String? {
        try {

            val obj =
                if (unsafeSerialization) controller.root
                else mapper.convertValue<Any>(controller.root, controller.root.type)!!

            val writer = if (Settings.prettyPrint) mapper.writerWithDefaultPrettyPrinter() else mapper.writer()
            return writer.writeValueAsString(obj)

        } catch (e: Throwable) {
            log("Failed to create object due to an exception.")
            log("${e.javaClass.simpleName}: ${e.message}")
            log(e)

            if (!mrBeanModule.enabled &&
                Settings.showMrBeanWarning == true &&
                e.message?.contains("Cannot construct instance of") == true
            ) {
                runLater {
                    information(
                        "Do you want to enable the Mr Bean module?",
                        "You cannot create abstract types without Mr Bean Module.",
                        YES, NO, NO_DISABLE_WARNING,
                        owner = FX.primaryStage
                    ) {
                        when (it) {
                            YES -> mrBeanModule.enabled = true
                            NO_DISABLE_WARNING -> Settings.showMrBeanWarning = false
                        }
                    }
                }
            }
        }
        return null
    }

    fun save() {
        val obj = toJson()
        if (obj == null) {
            error(
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
            log("Saved object to file ${file.canonicalPath}")
        }
    }

    fun validate() {
        log("Validating...")
        runAsync {

            val obj = toJson()
            if (obj != null) {
                log("Successfully created object!")
                log("json=$obj")
            }
        }
    }
}
