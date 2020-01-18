package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.stage.FileChooser
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.loader.DynamicClassLoader
import tornadofx.*


/**
 * Main view to control loading/unloading of object. Should also be able to edit setting (on [ObjectMapper])
 *
 * @author Elg
 */
object ControlPanelView : View("Control Panel") {

    override val root = vbox {
        val buttons = hbox {
            addClass(Styles.parent)
        }
        val output = scrollpane(fitToHeight = true, fitToWidth = true).textarea {
            editableProperty().set(false)
        }

        buttons += button {
            text = "Choose file to import class(es) from"
            setOnAction {
                val files = chooseFile(
                    "Choose conversations to load",
                    arrayOf(
                        FileChooser.ExtensionFilter("Jvm zip files", "*.jar", "*.zip"),
//                        FileChooser.ExtensionFilter("Jvm binary files", "*.class"),
                        FileChooser.ExtensionFilter("All files", "*")
                    ),
                    FileChooserMode.Multi
                )
                for (file in files) {
                    output.appendText("Loading file ${file.absolutePath}\n")
                    try {
                        DynamicClassLoader.loadFile(file)
                    } catch (e: Exception) {
                        output.appendText("Failed to load file ${file.absolutePath}\n$e")
                        e.printStackTrace()
                        continue
                    }

                    output.appendText("Successfully loaded file ${file.absolutePath}\n")
                    output.appendText(
                        "Found ${DynamicClassLoader.classesFromFile(file)?.size ?: 0} classes\n"
                    )

                }
            }
        }
        buttons += button("Clear") {
            setOnAction {
                output.clear()
            }
        }

    }
}
