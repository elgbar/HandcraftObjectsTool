package no.uib.inf219.example.gui.view

import com.fasterxml.jackson.core.type.TypeReference
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.data.prerequisite.AlwaysFalsePrerequisite
import no.uib.inf219.example.data.prerequisite.AlwaysTruePrerequisite
import no.uib.inf219.example.data.prerequisite.Prerequisite
import no.uib.inf219.example.data.prerequisite.logical.AndPrerequisite
import no.uib.inf219.example.gui.Main
import no.uib.inf219.example.gui.Styles
import org.yaml.snakeyaml.Yaml
import tornadofx.*


/**
 * @author Elg
 */
class SelectConversationView(val tabPane: TabPane) : View("") {

    val convs: ObservableList<Conversation> = FXCollections.observableArrayList()

    override val root = vbox()

    init {
        with(root) {
            addClass(Styles.parent)
            label("Load conversation") {
                addClass(Styles.headLineLabel)
            }
            val hBox = hbox() {
                addClass(Styles.parent)
            }
            val output = scrollpane(fitToHeight = true, fitToWidth = true).textarea {
                editableProperty().set(false)
            }
            hBox += button {
                text = "Choose file"
                setOnAction {
                    val files = chooseFile(
                        "Choose conversations to load",
                        arrayOf(
                            FileChooser.ExtensionFilter("YAML files", "*.yml", "*.yaml", "*.qst", "*.cnv"),
                            FileChooser.ExtensionFilter("All files", "*")
                        ),
                        FileChooserMode.Multi
                    )
                    for (file in files) {
                        output.appendText("Loading file ${file.absolutePath}\n")
                        output.appendText("content:\n")
                        output.appendText(file.readText())
                        output.appendText("\n")

                        try {
                            val conv = SerializationManager.load<Conversation>(file.readText())
                            convs += conv
                            output.appendText("Successfully loaded conversation!")
                        } catch (e: Exception) {
                            output.appendText("Failed to load conversation\n$e")
                            e.printStackTrace()
                            return@setOnAction
                        }
                    }
                }
            }
            hBox += button("Clear") {
                setOnAction {
                    output.clear()
                }
            }
            hBox += button("Test generic") {
                setOnAction {

                    val o = AndPrerequisite(listOf(AlwaysTruePrerequisite(), AlwaysTruePrerequisite()))
                    val o2 = AndPrerequisite(listOf(AlwaysTruePrerequisite(), AlwaysFalsePrerequisite(), o))

                    val dump = SerializationManager.dump(o2)
                    output.appendText(dump)
                    output.appendText("\n\n")
                    val oread: Prerequisite

                    try {
                        oread = SerializationManager.load(dump)
                    } catch (e: Exception) {
                        output.appendText("Failed to load object back\n$e")
                        e.printStackTrace()
                        return@setOnAction
                    }

                    output.appendText("\ntake 2: \n${SerializationManager.dump(oread)}\n")
                    output.appendText("Can use ${oread::class.simpleName}? ${oread.check()}${if (!oread.check()) " (due to '${oread.reason()}')" else ""}\n")
                }
            }

            hBox += button("Dump TEST CONV") {
                setOnAction {
                    val dump: String = SerializationManager.dump(Main.TEST_CONV)
                    val dump2: String
                    dump2 = try {
                        val conv = SerializationManager.load<Conversation>(dump)
                        convs += conv
                        output.appendText("eql test conv obj? ${conv == Main.TEST_CONV}\n")
                        SerializationManager.dump(conv);
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "failed to load it back in"
                    }
                    output.appendText("eql test conv str? ${dump2 == dump}\n")
                    output.appendText("\ndump\n $dump\n\n")
                    output.appendText("dump2\n\n $dump2")
                }
            }
            hBox += button("Dump End conv & exitResponse") {
                setOnAction {

                    val typeref: TypeReference<List<Response>> = object : TypeReference<List<Response>>() {}

                    val exitRespDump = SerializationManager.dump(Response.exitResponse)
                    output.appendText(exitRespDump)
                    output.appendText(
                        "\nEql when resp reload? " +
                                "${SerializationManager.mapper.readValue<List<Response>>(
                                    exitRespDump,
                                    typeref
                                ) == Response.exitResponse}\n"
                    )

                    output.appendText(SerializationManager.dump(SerializationManager.load<List<Response>>(exitRespDump)))

                    output.appendText("\n\n")

                    val endConvDump = SerializationManager.dump(Conversation.endConversation)
                    output.appendText(endConvDump)
                    output.appendText("\nEql when conv reload? ${SerializationManager.load<Conversation>(endConvDump) == Conversation.endConversation}\n")
                }
            }

            label("Loaded conversations") {
                addClass(Styles.headLineLabel)
            }
            scrollpane(fitToHeight = true, fitToWidth = true) {

                flowpane {
                    addClass(Styles.parent)
                    hgap = 3.0
                    vgap = 3.0

                    bindChildren(convs) {
                        val conv = it
                        val button =
                            button(if (conv.name.isEmpty()) "Conversation #${convs.indexOf(conv)}" else conv.name)
                        with(button) {
                            setOnAction {
                                val yaml = Yaml()
                                output.appendText("Conversation:\n ${yaml.dump(conv)}")
                                createTab(text, conv)
                            }
                        }
                        return@bindChildren button
                    }
                }
            }
        }
        convs.addAll(
            Main.TEST_CONV,
            Conversation.create("test")
        )
    }

    fun createTab(name: String, conv: Conversation) {
        tabPane.tab(name, BorderPane()) {
            add(ConversationView(this, conv).root)
            tabPane.selectionModel.select(this)
        }
    }


}
