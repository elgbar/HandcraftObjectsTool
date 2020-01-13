package no.uib.inf219.example.gui.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.prerequisite.AlwaysFalsePrereq
import no.uib.inf219.example.data.prerequisite.AlwaysTruePrerec
import no.uib.inf219.example.data.prerequisite.AndPrereq
import no.uib.inf219.example.data.prerequisite.Prerequisite
import no.uib.inf219.example.gui.Main
import no.uib.inf219.example.gui.Styles
import org.bukkit.configuration.file.YamlConfiguration
import org.yaml.snakeyaml.DumperOptions
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

                    val dumper = DumperOptions()
                    dumper.indent = 2
                    dumper.isPrettyFlow = true

                    val yaml = Yaml()

                    val o = AndPrereq()
                    val o2 = AndPrereq()
                    o2.others = listOf(AlwaysTruePrerec(), AlwaysTruePrerec())
                    o.others = listOf(AlwaysTruePrerec(), AlwaysFalsePrereq(), o2)

                    val dump = YamlConfiguration().yaml.dump(o)
                    output.appendText(dump)
                    output.appendText("\n\n")
                    val oread: Prerequisite

                    try {
                        oread = YamlConfiguration().yaml.load<Prerequisite>(dump)
                    } catch (e: Exception) {
                        output.appendText("Failed to load object back\n$e")
                        e.printStackTrace()
                        return@setOnAction
                    }

                    output.appendText("\ntake 2: \n${yaml.dump(oread)}\n")
                    output.appendText("Can use ${oread::class.simpleName}? ${oread.check()}${if (!oread.check()) " (due to '${oread.reason()}')" else ""}\n")
                }
            }

            hBox += button("Dump TEST CONV") {
                setOnAction {
                    val dump = SerializationManager.dump(Main.TEST_CONV)
                    output.appendText("\n\nTrying to load it back in\n\n")
                    output.appendText("eql test conv obj? ${SerializationManager.load<Conversation>(dump) == Main.TEST_CONV}\n")
                    val dump2 = SerializationManager.dump(SerializationManager.load<Conversation>(dump));
                    output.appendText("eql test conv str? ${dump2 == dump}\n")
                    output.appendText("dump\n $dump\n")
                    output.appendText("dump2\n $dump2")

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
            Conversation("test")
        )
    }

    fun createTab(name: String, conv: Conversation) {
        tabPane.tab(name, BorderPane()) {
            add(ConversationView(this, conv).root)
            tabPane.selectionModel.select(this)
        }
    }


}
