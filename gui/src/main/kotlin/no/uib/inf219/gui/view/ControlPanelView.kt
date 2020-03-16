package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.mrbean.MrBeanModule
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
import no.uib.inf219.gui.loader.ObjectMapperLoader
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

    private var orgMapper: ObjectMapper = mapper

    /**
     * What object mapper to use for serialization
     */
    var mapper: ObjectMapper
        get() = mapperProperty.get()
        set(value) {
            orgMapper = value
            mapperProperty.set(value.copy())
            updateMapper()
            FX.find<BackgroundView>().tabpane.closeAll()
        }

    var useMrBean = false.toProperty().apply {
        onChange {
            mapper = orgMapper
        }
    }

    private fun updateMapper() {
        ClassInformation.updateMapper()

        val beanModule = MrBeanModule();

        if (mapper.registeredModuleIds.contains(beanModule.typeId) && !useMrBean.value) {
            //It is enabled for the current ObjectMapper already do not enable it again
            useMrBean.set(true)
        } else if (useMrBean.value) {
            mapper.registerModule(MrBeanModule())
        }
    }

    init {
        updateMapper()
    }

    private val possibleMappers =
        SerializationManager.StdObjectMapper.values().mapTo(ArrayList()) { it.toString() to it.getObjectMapper() }
            .asObservable()

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
            addClass(Styles.parent)
            hbox {
                tooltip("Change what object mapper to use.\nWarning: Changing this will close all opened tabs.")

                label("Object Mapper Type ")

                combobox(
                    values = possibleMappers
                ) {

                    selectionModel.selectedItemProperty().onChange {
                        OutputArea.logln("Changing object mapper to ${it?.first}")
                        if (it != null) mapper = it.second
                    }
                    //wait till we're finished setting things up before changing the selected
                    runLater {
                        selectionModel.select(2)
                    }
                    cellFormat { text = it.first }
                }
            }
            checkbox("Use MrBean Module", useMrBean) {
                tooltip("If the MrBean module should be enabled. If it is the object mapper will allow to create instances from interfaces and abstract classes directly. This will not work with classes that are polymorphic and is annotated with @JsonTypeInfo.")
            }
        }

        OutputArea.logln("Example classes to load:")
        OutputArea.logln("java.lang.String")
        OutputArea.logln("no.uib.inf219.example.data.Conversation")
        OutputArea.logln("no.uib.inf219.example.data.Response")
        OutputArea.logln("no.uib.inf219.example.data.showcase.PrimitiveConvertsShowcase")
        OutputArea.logln("no.uib.inf219.example.data.showcase.PrimitiveDefaultValueShowcase")
        OutputArea.logln("no.uib.inf219.example.data.showcase.MapExample")
        OutputArea.logln("no.uib.inf219.example.data.showcase.GenericExample")
        OutputArea.logln("no.uib.inf219.example.data.showcase.Weather")
        OutputArea.logln("no.uib.inf219.example.data.prerequisite.AlwaysFalsePrerequisite")
    }

    private fun createTab(type: JavaType) {
        val editor: ObjectEditor
        try {
            editor = find(ObjectEditor::class, Scope(), "controller" to ObjectEditorController(type, null))
        } catch (e: Throwable) {
            OutputArea.log { "Failed to open tab due to an error $e" }
            error("Cannot serialize $type, failed to create an editor for the given type", e.message)
            return
        }
        FX.find<BackgroundView>().tabpane.tab("Edit ${type.rawClass.simpleName}", BorderPane()) {
            add(editor.root)
            tabPane.selectionModel.select(this)
        }
    }

    private fun loadFileSafely(file: File) {

        OutputArea.logln("Loading file ${file.absolutePath}")
        try {
            DynamicClassLoader.loadFile(file)
        } catch (e: Exception) {
            OutputArea.logln("Failed to load jar file ${file.absolutePath}")
            OutputArea.logln("$e")
            e.printStackTrace()
        }
        OutputArea.logln("Successfully loaded jar file ${file.absolutePath}")

        val mapper = ObjectMapperLoader.findObjectMapper(file) ?: return
        possibleMappers.add(file.nameWithoutExtension to mapper)
    }

    private fun File.copyInputStreamToFile(inputStream: InputStream) {
        this.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }
    }
}
