package no.uib.inf219.gui.view

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import javafx.scene.control.TextInputControl
import javafx.scene.layout.BorderPane
import javafx.scene.text.Text
import javafx.stage.FileChooser
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.MapClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
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

    override val root = borderpane {
        val buttons = hbox {
            addClass(Styles.parent)
        }
        val classChooser = hbox {
            addClass(Styles.parent)
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
                for (file in files) {
                    loadFileSafely(file)
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

                val file = createTempFile()
                file.copyInputStreamToFile(inp)
                loadFileSafely(file)

                output.appendText(
                    "Example classes to load:\n" +
                            "no.uib.inf219.example.data.Conversation\n" +
                            "no.uib.inf219.example.data.Response\n"
                )

                val respC = DynamicClassLoader.classFromName("no.uib.inf219.example.data.Response")!!
                val convC = DynamicClassLoader.classFromName("no.uib.inf219.example.data.Conversation")!!


                val respob: MapClassBuilder<Any> = MapClassBuilder(respC)
                val convob: MapClassBuilder<Any> = MapClassBuilder(convC)

                convob["name"] = "bname"
                convob["text"] = "blah blah"

                respob["response"] = "test response"
                convob["responses"] = listOf(respob)

                respob["conv"] = convob.toObject()

                println("ob.toObject() = ${convob.toObject()}")
            }
        }

        val clazzProperty = SimpleStringProperty("")


        classChooser += button("Load class") {
            setOnAction {
                val className = clazzProperty.value
                val clazz: Class<*>
                val cl: ClassLoader
                try {
                    val pair = DynamicClassLoader.classWithLoaderFromName(className)
                    if (pair == null) {
                        output.appendText("Failed to find a class with the name '${className}'\n")
                        return@setOnAction
                    }
                    clazz = pair.first
                    cl = pair.second
                } catch (e: IllegalStateException) {
                    output.appendText("Failed to load class due to $e\n")
                    e.printStackTrace()
                    return@setOnAction
                }
                output.appendText("Found $clazz\n")
                createTab(clazz)

//                val tfac: TypeFactory = TypeFactory.defaultInstance().withClassLoader(cl)
//                val jt: JavaType = tfac.constructType(clazz)
//                val jfac = JsonFactory.builder().build()
//                val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))
//
//                val cfg: SerializationConfig = mapper.serializationConfig
//                cfg.initialize(gen)
//
//                val ser: DefaultSerializerProvider =
//                    DefaultSerializerProvider.Impl().createInstance(cfg, mapper.serializerFactory)
//                seen.clear()
//                printStructure(jt, ser)
            }
        }

        classChooser += textfield {
            bind(clazzProperty)
            promptText = "Full class name"

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

    }

    private val seen: MutableSet<JavaType> = HashSet()

    fun printStructure(
        clazz: JavaType,
        ser: DefaultSerializerProvider,
        tab: String = ""
    ) {
        if (seen.contains(clazz)) return
        seen += clazz
        val jser: JsonSerializer<Any> = ser.findTypedValueSerializer(clazz, true, null)

        for ((i, prop) in jser.properties().withIndex()) {

            output.appendText("$tab$i: '${prop.name}' type: ${prop.type} required? ${prop.isRequired}\n")
            val nclazz = if (prop.type.contentType != null) prop.type.contentType else prop.type
            printStructure(nclazz, ser, "$tab\t")
        }
    }

    fun createTab(clazz: Class<*>) {
        tabPane.tab("Edit ${clazz.simpleName}", BorderPane()) {
            add(ObjectEditor(ObjectEditorController(clazz)).root)
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
