package no.uib.inf219.example.gui.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import no.uib.inf219.example.data.Conversation
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
                    }
                }
            }
            hBox += button("Clear") {
                setOnAction {
                    output.clear()
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
