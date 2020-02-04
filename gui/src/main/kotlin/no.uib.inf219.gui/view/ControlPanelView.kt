package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import javafx.scene.control.TextInputControl
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.loader.DynamicClassLoader
import tornadofx.*
import java.io.File
import java.io.InputStream


/**
 * Main view to control loading/unloading of object. Should also be able to edit setting (on [ObjectMapper])
 *
 * @author Elg
 */
object ControlPanelView : View("Control Panel") {

    lateinit var tabPane: TabPane
        internal set

    lateinit var output: TextInputControl
        private set

    /**
     * What object mapper to use for serialization
     */
    var mapper: ObjectMapper = SerializationManager.stdMapper
        set(value) {
            field = value
            ClassInformation.updateMapper()
        }

    override val root = borderpane {
        val buttons = hbox {
            addClass(Styles.parent)
        }
        val classChooser = hbox {
            addClass(Styles.parent)
            fitToParentWidth()
        }
        output = scrollpane(fitToHeight = true, fitToWidth = true).textarea() {
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
                runAsync {
                    for (file in files) {
                        loadFileSafely(file)
                    }
                }
            }
        }
        buttons += button("Clear") {
            setOnAction {
                output.clear()
            }
        }
        buttons += button("Load Example") {
            setOnAction {
                val inp = ControlPanelView::class.java.getResourceAsStream("/example.jar")
                if (inp == null) {
                    output.appendText("Failed to find example jar\n")
                    return@setOnAction
                }
                runAsync {

                    val file = createTempFile()
                    file.copyInputStreamToFile(inp)
                    loadFileSafely(file)
                } ui {
                    output.appendText(
                        "Example classes to load:\n" +
                                "no.uib.inf219.example.data.Conversation\n" +
                                "no.uib.inf219.example.data.Response\n"
                    )
                }


            }
        }

        val clazzProperty = SimpleStringProperty("")


        classChooser += button("Load class") {
            setOnAction {
                runAsync {
                    val className = clazzProperty.value
                    val clazz: Class<*>
                    try {
                        val pair = DynamicClassLoader.classWithLoaderFromName(className)
                        if (pair == null) {
                            ui {
                                output.appendText("Failed to find a class with the name '${className}'\n")
                            }
                            return@runAsync
                        }
                        clazz = pair.first
                    } catch (e: IllegalStateException) {
                        ui {
                            output.appendText("Failed to load class due to $e\n")
                            e.printStackTrace()
                        }
                        return@runAsync
                    }

                    ui {
                        output.appendText("Found $clazz\n")
                        createTab(ClassInformation.toJavaType(clazz))
                    }
                }
            }
        }

        classChooser += textfield {
            bind(clazzProperty)
            promptText = "Full class name"
            text = "no.uib.inf219.example.data.Conversation"
            hgrow = Priority.ALWAYS
        }

    }

    fun createTab(type: JavaType) {
        tabPane.tab("Edit ${type.rawClass.simpleName}", BorderPane()) {
            add(ObjectEditor(ObjectEditorController(type)).root)
            tabPane.selectionModel.select(this)
        }
    }

    fun loadFileSafely(file: File) {

        output.appendText("Loading file ${file.absolutePath}\n")
        try {
            DynamicClassLoader.loadFile(file)
        } catch (e: Exception) {
            output.appendText("Failed to load jar file ${file.absolutePath}\n$e")
            e.printStackTrace()
        }
        output.appendText("Successfully loaded jar file ${file.absolutePath}\n")
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
}
