package no.uib.inf219.example.gui.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.gui.Main
import no.uib.inf219.example.gui.Styles
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
            val but = button {
                text = "Choose file"
            }
            val selSP = scrollpane(fitToHeight = true, fitToWidth = true) {
                style {
                    minHeight = 120.px
                }
            }.flowpane() {
                hgap = 3.0
                vgap = 3.0
            }
            with(but) {
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
                        selSP.button("File ${file.name}") {
                            setOnAction {
                                log.info(file.readText())
                            }
                        }
                    }
                    requestLayout()
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
            Conversation("test"),
            Conversation("test2", "name"),
            Conversation("test3"),
            Conversation("test4", "4"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test5", "a  convaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
            Conversation("test6"),
            Conversation("test7"),
            Conversation("test8", "inside nr 8")
        )
    }

    fun createTab(name: String, conv: Conversation) {
        tabPane.tab(name, BorderPane()) {
            add(ConversationView(this, conv).root)
            tabPane.selectionModel.select(this)
        }
    }
}
