package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.mrbean.MrBeanModule
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.extra.Persistent
import no.uib.inf219.extra.closeAll
import no.uib.inf219.extra.type
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.ems
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

    val tabMap = HashMap<Tab, ObjectEditorBackgroundView>()

    /**
     * What object mapper to use for serialization
     */
    var mapper: ObjectMapper
        get() = mapperProperty.get()
        set(value) {
            orgMapper = value
            mapperProperty.set(value.copy())
            updateMapper()
            tabMap.clear()
            FX.find<BackgroundView>().tabPane.closeAll()
        }

    internal var useMrBeanProperty = booleanProperty().apply {
        onChange {
            //this forces an call to #updateMapper()
            mapper = orgMapper
        }
    }
    var useMrBean by useMrBeanProperty

    private var useAfterburnerProp = booleanProperty(true)
    private var useAfterburner by useAfterburnerProp

    var unsafeSerialization = false.toProperty()

    private fun updateMapper() {
        ClassInformation.updateMapper()

        val beanModule = MrBeanModule()

        if (mapper.registeredModuleIds.contains(beanModule.typeId) && !useMrBean) {
            //It is enabled for the current ObjectMapper already do not enable it again
            useMrBean = true
        } else if (useMrBean) {
            mapper.registerModule(beanModule)
        }

        val afterBurnerModule = AfterburnerModule()

        if (mapper.registeredModuleIds.contains(afterBurnerModule.typeId) && !useAfterburner) {
            //It is enabled for the current ObjectMapper already do not enable it again
            useAfterburner = true
        } else if (useAfterburner) {
            mapper.registerModule(afterBurnerModule)
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
                                ui {
                                    warning("No jar files found in ${folder.path}")
                                }
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
                        error("Failed to find example jar")
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
                            clazz = DynamicClassLoader.loadClass(className)
                        } catch (e: Throwable) {
                            OutputArea.logln { "Failed to load class $className due to an error $e" }
                            ui {
                                warning(
                                    "Failed to find a class with the name '${className}'",
                                    "Due to exception ${e.javaClass.name}: ${e.localizedMessage}\n" +
                                            "\n" +
                                            "Have you remembered to load the expected jar(s)?"
                                )
                            }
                            return@runAsync
                        }

                        ui {
                            OutputArea.logln("Found $clazz")
                            createTab(clazz.type())
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
            style {
                fontSize = 1.6.ems
            }
        }

        val closeTabsWarningMsg = "Warning: Changing this will close all opened tabs."

        vbox {
            addClass(Styles.parent)

            separator()
            label("Object Mapper") {
                style {
                    fontSize = 1.3.ems
                }
            }

            hbox {

                style {
                    spacing = 0.333.ems
                }

                combobox(
                    values = possibleMappers
                ) {

                    tooltip(
                        "Change what object mapper to use. It is possible to have your own object mapper loaded from\n" +
                                "any loaded files. To find more information see the README." +
                                "\n" +
                                closeTabsWarningMsg
                    )

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

            separator()
            label("Modules") {
                style {
                    fontSize = 1.3.ems
                }
            }

            hbox {
                style {
                    spacing = 0.333.ems
                }
                checkbox("Use MrBean Module", useMrBeanProperty) {
                    tooltip(
                        "Mr Bean is an extension that implements support for \"POJO type materialization\"; ability for databinder to\n" +
                                "construct implementation classes for Java interfaces and abstract classes, as part of deserialization.\n" +
                                "This will not work with classes that are polymorphic and is annotated with @JsonTypeInfo.\n" +
                                "Enabling this will allow you to select interfaces and abstract classes in the class selection interface.\n" +
                                "\n" +
                                closeTabsWarningMsg
                    )
                }

                checkbox("Use Afterburner Module", useAfterburnerProp) {
                    tooltip(
                        "Module that will add dynamic bytecode generation for standard Jackson POJO serializers and deserializers,\n" +
                                "eliminating majority of remaining data binding overhead. It is recommenced to have this enabled, \n" +
                                "but can be disabled if there are any problems with it\n" +
                                "\n" +
                                closeTabsWarningMsg
                    )
                }
            }
            separator()
            label("Misc") {
                style {
                    fontSize = 1.3.ems
                }
            }

            checkbox("Unsafe Serialization", unsafeSerialization) {
                tooltip(
                    "If the objects should be serialized without checking if it can be deserialized.\n" +
                            "Sometimes is not possible to check if an object can be deserialized in this GUI."
                )
            }
        }

        OutputArea.logln("Example classes to load:")
        OutputArea.logln("java.lang.String")
        OutputArea.logln("java.util.UUID")
        OutputArea.logln("no.uib.inf219.example.data.Conversation")
        OutputArea.logln("no.uib.inf219.example.data.Response")
        OutputArea.logln("no.uib.inf219.example.data.showcase.JsonValueExample")
        OutputArea.logln("no.uib.inf219.example.data.showcase.PrimitiveConvertsShowcase")
        OutputArea.logln("no.uib.inf219.example.data.showcase.PrimitiveDefaultValueShowcase")
        OutputArea.logln("no.uib.inf219.example.data.showcase.MapExample")
        OutputArea.logln("no.uib.inf219.example.data.showcase.GenericExample")
        OutputArea.logln("no.uib.inf219.example.data.showcase.Weather")
        OutputArea.logln("no.uib.inf219.example.data.prerequisite.AlwaysFalsePrerequisite")
    }

    private fun createTab(type: JavaType) {
        val editorBackgroundView: ObjectEditorBackgroundView
        try {
            editorBackgroundView =
                find(ObjectEditorBackgroundView::class, Scope(), "controller" to ObjectEditorController(type, null))
        } catch (e: Throwable) {
            OutputArea.logln { "Failed to open tab due to an error $e" }
            e.printStackTrace()
            error(
                "Can not serialize ${type.rawClass}",
                "Failed to create an editor for the given class.\n" +
                        "\n" +
                        "Threw ${e.javaClass.simpleName}: ${e.message}"
            )
            return
        }
        FX.find<BackgroundView>().tabPane.tab("Edit ${type.rawClass.simpleName}", BorderPane()) {

            setOnClosed {
                tabMap[it.target]?.save()
            }

            this += editorBackgroundView.root
            tabMap[this] = editorBackgroundView
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
