package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.closeAll
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

    val tabPane: BackgroundView by inject()

    private val mapperProperty by lazy { SimpleObjectProperty<ObjectMapper>(SerializationManager.jsonMapper) }

    /**
     * What object mapper to use for serialization
     */
    var mapper: ObjectMapper
        get() = mapperProperty.get()
        set(value) {
            mapperProperty.set(value)
            ClassInformation.updateMapper()
            FX.find<BackgroundView>().tabpane.closeAll()
        }

    override val root = vbox {
        hbox {
            addClass(Styles.parent)
            button {
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
            this += OutputArea.clearButton()
            button("Load Example") {
                setOnAction {
                    val inp = ControlPanelView::class.java.getResourceAsStream("/example.jar")
                    if (inp == null) {
                        OutputArea.logln("Failed to find example jar")
                        return@setOnAction
                    }
                    runAsync {

                        val file = createTempFile()
                        file.copyInputStreamToFile(inp)
                        loadFileSafely(file)
                    } ui {
                        OutputArea.logln("Example classes to load:")
                        OutputArea.logln("no.uib.inf219.example.data.Conversation")
                        OutputArea.logln("no.uib.inf219.example.data.Response")
                    }


                }
            }
        }
        hbox {
            addClass(Styles.parent)
            val clazzProperty = SimpleStringProperty("")

            button("Load class") {
                setOnAction {
                    runAsync {
                        val className = clazzProperty.value
                        val clazz: Class<*>
                        try {
                            val pair = DynamicClassLoader.classWithLoaderFromName(className)
                            if (pair == null) {
                                ui {
                                    OutputArea.logln("Failed to find a class with the name '${className}'")
                                }
                                return@runAsync
                            }
                            clazz = pair.first
                        } catch (e: IllegalStateException) {
                            ui {
                                OutputArea.logln("Failed to load class due to $e")
                                e.printStackTrace()
                            }
                            return@runAsync
                        }

                        ui {
                            OutputArea.logln("Found $clazz")
                            createTab(ClassInformation.toJavaType(clazz))
                        }
                    }
                }
            }
            textfield {
                bind(clazzProperty)
                promptText = "Full class name"
                text = "no.uib.inf219.example.data.Conversation"
                hgrow = Priority.ALWAYS
            }
        }

        label("Settings") {
            addClass(Styles.headLineLabel)
        }

        vbox {
            hbox {
                tooltip("Change what object mapper to use.\nWarning: Changing this will close all opened tabs.")
                addClass(Styles.parent)

                label("Object Mapper Type ")

                combobox(
                    property = SerializationManager.StdObjectMapper.fromObjectMapper(mapper).toProperty(),
                    values = SerializationManager.StdObjectMapper.values().asList()
                ) {
                    selectionModel.selectedItemProperty().onChange {
                        OutputArea.logln("Changing object mapper to ${it?.toString()}")
                        if (it != null) mapper = it.getObjectMapper()
                    }
                }
            }
        }

    }

    fun createTab(type: JavaType) {
        tabPane.tabpane.tab("Edit ${type.rawClass.simpleName}", BorderPane()) {
            add(ObjectEditor(ObjectEditorController(type)).root)
            tabPane.selectionModel.select(this)
        }
    }

    fun loadFileSafely(file: File) {

        OutputArea.logln("Loading file ${file.absolutePath}")
        try {
            DynamicClassLoader.loadFile(file)
        } catch (e: Exception) {
            OutputArea.logln("Failed to load jar file ${file.absolutePath}")
            OutputArea.logln("$e")
            e.printStackTrace()
        }
        OutputArea.logln("Successfully loaded jar file ${file.absolutePath}")
    }

    fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
}
