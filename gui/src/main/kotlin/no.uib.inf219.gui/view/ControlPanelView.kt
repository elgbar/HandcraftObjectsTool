package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.scene.text.Text
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

    override val root = borderpane {
        val buttons = hbox {
            addClass(Styles.parent)
        }
        val classChooser = hbox {
            addClass(Styles.parent)
        }
        val output = scrollpane(fitToHeight = true, fitToWidth = true).textarea() {
            editableProperty().set(false)
        }
        top = borderpane {
            top = buttons
            center = classChooser
        }
        center = output

        buttons += button {
            text = "import jar"
            setOnAction {
                val files = chooseFile(
                    "Choose jar to load",
                    arrayOf(
                        FileChooser.ExtensionFilter("Jvm zip files", "*.jar", "*.zip"),
                        FileChooser.ExtensionFilter("All files", "*")
                    ),
                    FileChooserMode.Multi
                )
                for (file in files) {
                    output.appendText("Loading file ${file.absolutePath}\n")
                    try {
                        DynamicClassLoader.loadFile(file)
                    } catch (e: Exception) {
                        output.appendText("Failed to load jar file ${file.absolutePath}\n$e")
                        e.printStackTrace()
                        continue
                    }

                    output.appendText("Successfully loaded jar file ${file.absolutePath}\n")
                }
            }
        }
        buttons += button("Clear") {
            setOnAction {
                output.clear()
            }
        }

        val clazzProperty = SimpleStringProperty("")


        val loadButton = button("Load class") {
            setOnAction {
                val className = clazzProperty.value
                val clazz: Class<*>?
                try {
                    clazz = DynamicClassLoader.classForName(className)
                } catch (e: IllegalStateException) {
                    output.appendText("Failed to load class due to $e\n")
                    return@setOnAction
                }
                if (clazz == null) {
                    output.appendText("Failed to find a class with the name '${className}'\n")
                    return@setOnAction
                }
                output.appendText("Found $clazz\n")
            }
        }

        classChooser += textfield {
            bind(clazzProperty)
            promptText = "Full class name"

            //TODO remove me, just make easier to load a class
            output.appendText("no.uib.inf219.example.data.Conversation\n")
            output.appendText("no.uib.inf219.example.data.Response\n")

            setOnKeyTyped {
                Platform.runLater {
                    val text = Text(clazzProperty.value)
                    text.font = font // Set the same font, so the size is the same
                    val width: Double =
                        (text.layoutBounds.width // This big is the Text in the TextField
                                + padding.left + padding.right // Add the padding of the TextField
                                + 2.0) // Add some spacing
                    prefWidth = width // Set the width
                    positionCaret(caretPosition) // If you remove this line, it flashes a little bit
//                    loadButton.fire()
                }
            }

        }
        classChooser += loadButton

    }
}
