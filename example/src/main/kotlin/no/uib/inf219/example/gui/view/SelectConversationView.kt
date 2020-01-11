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
            padding = insets(3)
            spacing = 2.0
            label("Load conversation") {
                addClass(Styles.headLineLabel)
            }
            val vbb = hbox {
                style {
                    padding = box(2.px)
                    spacing = 2.px
                }
            }
            val output = scrollpane(fitToHeight = true, fitToWidth = true).textarea {
                editableProperty().set(false)
            }
            vbb += button {
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
            vbb += button("Clear") {
                setOnAction {
                    output.clear()
                }
            }

            label("Loaded conversations") {
                addClass(Styles.headLineLabel)
            }
            scrollpane(fitToHeight = true, fitToWidth = true) {

                flowpane {
                    bindChildren(convs) {
                        val conv = it
                        val button =
                            button(if (conv.name.isEmpty()) "Conversation #${convs.indexOf(conv)}" else conv.name)
                        with(button) {
                            minWidth = 120.0 //magic number is magic
                            setOnAction {
                                val yaml = Yaml()
                                output.appendText("Conversation:\n ${yaml.dump(conv)}")
                                createTab(text, conv)
                            }
                        }
                        return@bindChildren button
                    }
                    style {
                        padding = box(1.px)
                    }
                    hgap = 3.0
                    vgap = 3.0
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
