/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.mrbean.AbstractTypeMaterializer
import com.fasterxml.jackson.module.mrbean.MrBeanModule
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import javafx.stage.FileChooser
import no.uib.inf219.extra.SerializationManager
import no.uib.inf219.extra.copyInputStreamToFile
import no.uib.inf219.extra.type
import no.uib.inf219.gui.Settings.lastFolderLoaded
import no.uib.inf219.gui.Settings.prettyPrintProp
import no.uib.inf219.gui.Settings.printStackTraceOnErrorProp
import no.uib.inf219.gui.Settings.unsafeSerializationProp
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.ems
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.loader.DynamicClassLoader
import no.uib.inf219.gui.loader.ObjectMapperLoader
import no.uib.inf219.gui.view.select.ClassSelectorView
import no.uib.inf219.gui.view.settings.ModuleSetting
import tornadofx.*
import java.io.File
import java.io.FileFilter
import java.lang.invoke.MethodHandles
import kotlin.collections.set


/**
 * Main view to control loading/unloading of object. Should also be able to edit setting (on [ObjectMapper])
 *
 * @author Elg
 */
object ControlPanelView : View("Control Panel") {

    /**
     * tab to editor map
     */
    val tabMap = HashMap<Tab, ObjectEditorBackgroundView>()


    const val SHOW_DEBUG_NODES = false

    /**
     * List of known mappers
     */
    private val knownObjectMappers =
        SerializationManager.StdObjectMapper.values().mapTo(ArrayList()) {
            it.toString() to it.getObjectMapper()
        }.asObservable()

    //////////////////////
    // mapper variables //
    //////////////////////


    private val mapperProperty by lazy {
        SimpleObjectProperty(SerializationManager.kotlinJson)
    }
    private var orgMapper: ObjectMapper = mapper

    /**
     * The object mapper to use for serialization
     */
    var mapper: ObjectMapper
        get() = mapperProperty.get()
        set(value) {
            orgMapper = value.copy()
            mapperProperty.set(value)
            updateMapper()
        }


    /////////////////////
    // Module Settings //
    /////////////////////

    val mrBeanModule = ModuleSetting(
        false,
        """Mr Bean is an extension that implements support for "POJO type materialization"; ability for databinder to
            construct implementation classes for Java interfaces and abstract classes, as part of deserialization.
            This will not work with classes that are polymorphic and is annotated with @JsonTypeInfo.
            Enabling this will allow you to select interfaces and abstract classes in the class selection interface.""".trim()
    ) { MrBeanModule(AbstractTypeMaterializer(DynamicClassLoader)) }

    private val afterburnerModule = ModuleSetting(
        true,
        """Module that will add dynamic byte code generation for standard Jackson POJO serializers and deserializers,
        eliminating majority of remaining data binding overhead. It is recommenced to have this enabled, 
        but can be disabled if there are any problems with it""".trimMargin()
    ) { AfterburnerModule() }

    //After adding a module to the list above you must also add it to this list!
    private val moduleSettings = listOf(mrBeanModule, afterburnerModule)

    init {
        updateMapper()
    }

    /**
     * Rebuild mapper with the current settings.
     * Should be called when a setting related to [mapper] is updated
     */
    fun reloadMapper() {
        //this forces an call to #updateMapper()
        mapper = orgMapper
    }

    private fun updateMapper() {
        ClassInformation.updateMapper()


        //We do not want to register modules twice so make sure duplicates are ignored
        //For now restore the original config after this method, but maybe this should be
        // just a test, throwing if it is disabled?
        val oldIgnore = mapper.isEnabled(IGNORE_DUPLICATE_MODULE_REGISTRATIONS)
        mapper.configure(IGNORE_DUPLICATE_MODULE_REGISTRATIONS, true)

        for (module in moduleSettings) {
            if (module.enabled) {
                mapper.registerModule(module.createModule())
            }
        }

        for (module in moduleSettings) {
            if (!module.enabled && mapper.registeredModuleIds.contains(module.typeId)) {
                module.enabled = true
            }
        }

        mapper.configure(IGNORE_DUPLICATE_MODULE_REGISTRATIONS, oldIgnore)
    }

    override val root = vbox {
        val classNameProperty = SimpleStringProperty("")
        hbox {
            addClass(Styles.parent)
            button("Import jar") {
                tooltip("Import all selected files")
                action {
                    val files = chooseFile(
                        "Choose jar to load",
                        arrayOf(
                            FileChooser.ExtensionFilter("Jvm zip files", "*.jar", "*.zip"),
                            FileChooser.ExtensionFilter("All files", "*")
                        ),
                        lastFolderLoaded,
                        FileChooserMode.Multi
                    )
                    if (files.isNotEmpty()) {
                        lastFolderLoaded = files[0].parentFile
                    }
                    runAsync {
                        for (file in files) {
                            loadFileSafely(file)
                        }
                    }
                }
            }
            button("Import jars") {
                tooltip("Import all jar files from a directory")
                setOnAction {
                    val folder = chooseDirectory("Choose jar to load", lastFolderLoaded)
                    if (folder != null) {
                        lastFolderLoaded = folder
                    }
                    runAsync {
                        val files = folder?.listFiles(FileFilter { it.extension == "jar" })
                        if (files.isNullOrEmpty()) {
                            if (files != null) {
                                ui {
                                    error("No jar files found in ${folder.path}", owner = FX.primaryStage)
                                }
                                LoggerView.log("No jar files found in ${folder.path}")
                            }
                            return@runAsync
                        }
                        for (file in files) {
                            loadFileSafely(file)
                        }
                    }
                }
            }
            if (SHOW_DEBUG_NODES) {
                button("Load Example") {
                    setOnAction {
                        val inp = MethodHandles.lookup().lookupClass().getResourceAsStream("/example.jar")
                        if (inp == null) {
                            error(
                                "Failed to find example jar",
                                owner = FX.primaryStage
                            )
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
            textfield {
                bind(classNameProperty)
                promptText = "Full class name"
                if (SHOW_DEBUG_NODES) {
                    text = "no.uib.inf219.example.data.Conversation"
                }
                hgrow = Priority.ALWAYS
            }
        }
        hbox {
            addClass(Styles.parent)

            fun loadType(): JavaType? {
                val className: String = classNameProperty.value ?: ""

                val firstChar = className.first()
                val errMsg = when {
                    className.contains(' ') ->
                        "Class names cannot contain space"
                    className.isBlank() ->
                        "Given classname is blank"
                    //Allow '[' to allow loading classes with their fully qualified names
                    firstChar != '[' && !firstChar.isJavaIdentifierStart() ->
                        "A class cannot start with the character $firstChar"
                    else -> null
                }

                if (errMsg != null) {
                    runLater {
                        error(
                            """
                            Failed to find a class with the name '${className}'
                            
                            $errMsg
                            """.trimIndent(), owner = FX.primaryStage
                        )
                    }
                    return null
                }

                return try {
                    DynamicClassLoader.parseClassName(className)
                } catch (e: Throwable) {
                    LoggerView.log { "Failed to load class $className due to an error $e" }
                    runLater {
                        error(
                            """
                            Failed to find a class with the name '${className}'
                            
                            Due to exception ${e.javaClass.name}: ${e.localizedMessage}
                            Have you remembered to load the expected jar(s)?
                            """.trimIndent(), owner = FX.primaryStage
                        )
                    }
                    null
                }
            }

            button("Create new object") {
                action {
                    runAsync {
                        val type = loadType()
                        if (type != null) {
                            LoggerView.log("Found $type")
                            runLater {
                                createTab(type, null)
                            }
                        }
                    }
                }
            }
            button("Load existing object") {
                action {
                    runAsync {
                        val type = loadType()
                        if (type != null) {
                            LoggerView.log("Found $type")

                            ui {
                                val files = chooseFile(
                                    "Choose file to load",
                                    arrayOf(
                                        FileChooser.ExtensionFilter("json", "*.json"),
                                        FileChooser.ExtensionFilter("yaml", "*.yaml", "*.yml"),
                                        FileChooser.ExtensionFilter("All files", "*")
                                    ),
                                    lastFolderLoaded,
                                    FileChooserMode.Single
                                )
                                if (files.isEmpty()) {
                                    return@ui
                                }
                                lastFolderLoaded = files[0].parentFile

                                val obj = mapper.readValue<Any>(files[0], type) ?: return@ui

                                runLater {
                                    createTab(type, obj)
                                }
                            }
                        }
                    }
                }
            }
        }

        label("Settings") {
            addClass(Styles.headLineLabel)
            addClass(Styles.parent)
            style {
                fontSize = 1.6.ems
            }
        }

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
                    values = knownObjectMappers
                ) {

                    tooltip(
                        "Change what object mapper to use. It is possible to have your own object mapper loaded from\n" +
                                "any loaded files. To find more information see the README."
                    )

                    selectionModel.selectedItemProperty().onChange {
                        LoggerView.log("Changing object mapper to ${it?.first}")
                        if (it != null) mapper = it.second
                    }
                    //wait till we're finished setting things up before changing the selected
                    runLater {
                        selectionModel.select(2)
                    }
                    cellFormat { text = it.first }
                }
            }

            fun settingsSection(title: String, op: EventTarget.() -> Unit) {
                separator()
                label(title) { style { fontSize = 1.3.ems } }

                scrollpane(fitToHeight = true, fitToWidth = true) {
                    addClass(Styles.invisibleScrollpaneBorder)
                    val stage = FX.getPrimaryStage(scope)
                    if (stage != null) {
                        maxWidthProperty().bind(primaryStage.widthProperty())
                        maxHeightProperty().bind(primaryStage.heightProperty() / 5)
                    }
                    flowpane {
                        orientation = Orientation.VERTICAL
                        addClass(Styles.flowPane)

                        this.op()
                    }
                }
            }

            settingsSection("Modules") {
                for (modules in moduleSettings) {
                    checkbox("Use ${modules.name}", modules.enabledProp) {
                        tooltip(modules.tooltip)
                    }
                }
            }

            settingsSection("Misc") {
                checkbox("Unsafe Serialization", unsafeSerializationProp) {
                    tooltip(
                        "If the objects should be serialized without checking if it can be deserialized.\n" +
                                "Sometimes is not possible to check if an object can be deserialized in this GUI."
                    )
                }

                checkbox("Pretty Print Serialized Object", prettyPrintProp) {
                    tooltip(
                        "If the saved output will be using pretty printing."
                    )
                }

                checkbox("Print stack trace of exceptions", printStackTraceOnErrorProp) {
                    tooltip(
                        "If the stacktrace should be printed when an exception is encountered."
                    )
                }
            }
        }

        if (SHOW_DEBUG_NODES) {
            LoggerView.log("Example classes to load:")
            LoggerView.log("java.lang.String")
            LoggerView.log("java.lang.Integer")
            LoggerView.log("java.util.UUID")
            LoggerView.log("no.uib.inf219.example.data.Conversation")
            LoggerView.log("no.uib.inf219.example.data.Response")
            LoggerView.log("no.uib.inf219.example.data.showcase.JsonValueExample")
            LoggerView.log("no.uib.inf219.example.data.showcase.PrimitiveConvertsShowcase")
            LoggerView.log("no.uib.inf219.example.data.showcase.PrimitiveDefaultValueShowcase")
            LoggerView.log("no.uib.inf219.example.data.showcase.MapExample")
            LoggerView.log("no.uib.inf219.example.data.showcase.GenericExample")
            LoggerView.log("no.uib.inf219.example.data.showcase.Weather")
            LoggerView.log("no.uib.inf219.example.data.prerequisite.AlwaysFalsePrerequisite")
        }
    }

    private fun createTab(type: JavaType, obj: Any?) {

        val editorBackgroundView: ObjectEditorBackgroundView
        try {
            editorBackgroundView =
                find(ObjectEditorBackgroundView::class, Scope(), "controller" to ObjectEditorController(type, obj))
        } catch (e: Throwable) {
            LoggerView.log { "Failed to open tab due to an error $e" }
            LoggerView.log(e)
            error(
                "Can not serialize ${type.rawClass}",
                "Failed to create an editor for the given class.\n" +
                        "\n" +
                        "Threw ${e.javaClass.simpleName}: ${e.message}",
                owner = FX.primaryStage
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

        DynamicClassLoader.loadFile(file)

        val mapper = ObjectMapperLoader.findObjectMapper(file) ?: return
        knownObjectMappers.add(file.nameWithoutExtension to mapper)
    }
}
