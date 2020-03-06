package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.extra.Persistent
import no.uib.inf219.extra.closeAll
import no.uib.inf219.extra.type
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.loader.DynamicClassLoader
import tornadofx.*
import java.io.File
import java.io.FileFilter
import java.io.InputStream
import java.lang.invoke.MethodHandles


/**
 * Main view to control loading/unloading of object. Should also be able to edit setting (on [ObjectMapper])
 *
 * @author Elg
 */
object ControlPanelView : View("Control Panel") {

    private val mapperProperty by lazy { SimpleObjectProperty(SerializationManager.kotlinJson) }

    private var lastFile: File? by Persistent()

    /**
     * What object mapper to use for serialization
     */
    var mapper: ObjectMapper
        get() = mapperProperty.get()
        set(value) {
            mapperProperty.set(value)
            updateMapper()
            FX.find<BackgroundView>().tabpane.closeAll()
        }

    private fun updateMapper() {

        ClassInformation.updateMapper()

//        val module = SimpleModule()
//        module.setSerializerModifier(ClassBuilderBeanSerializerModifier())
//        mapper.registerModule(module)
    }

    init {
        updateMapper()
    }

    override val root = vbox {
        val classNameProperty = SimpleStringProperty("")
        hbox {
            addClass(Styles.parent)

            button {
                text = "Import jar"
                tooltip("Import all selected files")
                setOnAction {
                    val files = chooseFile(
                        "Choose jar to load",
                        arrayOf(
                            FileChooser.ExtensionFilter("Jvm zip files", "*.jar", "*.zip"),
                            FileChooser.ExtensionFilter("All files", "*")
                        ),
                        lastFile,
                        FileChooserMode.Multi
                    )
                    if (files.isNotEmpty()) {
                        lastFile = files[0].parentFile
                    }
                    runAsync {
                        for (file in files) {
                            loadFileSafely(file)
                        }
                    }
                }
            }
            button {
                text = "Import jars"
                tooltip("Import all jar files from a directory")
                setOnAction {
                    val folder = chooseDirectory("Choose jar to load", lastFile)
                    if (folder != null) {
                        lastFile = folder
                    }
                    runAsync {
                        val files = folder?.listFiles(FileFilter { it.extension == "jar" })
                        if (files.isNullOrEmpty()) {
                            if (files != null) {
                                OutputArea.logln("No jar files found in ${folder.path}")
                            }
                            return@runAsync
                        }
                        for (file in files) {
                            loadFileSafely(file)
                        }
                    }
                }
            }
            this += OutputArea.clearButton()
            button("Load Example") {
                setOnAction {
                    val inp = MethodHandles.lookup().lookupClass().getResourceAsStream("/example.jar")
                    if (inp == null) {
                        OutputArea.logln("Failed to find example jar")
                        return@setOnAction
                    }
                    runAsync {
                        val file = createTempFile()
                        file.copyInputStreamToFile(inp)
                        loadFileSafely(file)
                    }
                }
            }
        }
        hbox {
            addClass(Styles.parent)
            button("Choose class") {
                action {
                    val subclass = tornadofx.find<ClassSelectorView>().subtypeOf(Any::class.type(), true)
                    val javaName = subclass?.rawClass?.canonicalName ?: return@action
                    classNameProperty.set(javaName)
                }
            }
        }
        hbox {
            addClass(Styles.parent)

            button("Load class") {
                setOnAction {
                    runAsync {
                        val className = classNameProperty.value
                        val clazz: Class<*>
                        try {
                            try {
                                clazz = DynamicClassLoader.loadClass(className)
                            } catch (e: Throwable) {
                                ui {
                                    OutputArea.logln("Failed to find a class with the name '${className}'")
                                }
                                return@runAsync
                            }
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
                bind(classNameProperty)
                promptText = "Full class name"
                text = "no.uib.inf219.example.data.Conversation" //TODO remove
                hgrow = Priority.ALWAYS
            }
        }

        label("Settings") {
            addClass(Styles.headLineLabel)
            addClass(Styles.parent)
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

        OutputArea.logln("Example classes to load:")
        OutputArea.logln("no.uib.inf219.example.data.Conversation")
        OutputArea.logln("no.uib.inf219.example.data.Response")
        OutputArea.logln("no.uib.inf219.example.data.showcase.PrimitiveConvertsShowcase")
        OutputArea.logln("no.uib.inf219.example.data.showcase.PrimitiveDefaultValueShowcase")
        OutputArea.logln("no.uib.inf219.example.data.showcase.MapExample")
        OutputArea.logln("no.uib.inf219.example.data.showcase.GenericExample")
        OutputArea.logln("java.lang.String")
    }

    fun createTab(type: JavaType) {
        FX.find<BackgroundView>().tabpane.tab("Edit ${type.rawClass.simpleName}", BorderPane()) {
            add(find<ObjectEditor>(params = *arrayOf("controller" to ObjectEditorController(type))).root)
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
