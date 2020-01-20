package no.uib.inf219.example.gui.view

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.gui.Main
import no.uib.inf219.example.gui.Styles
import no.uib.inf219.example.gui.Styles.Companion.conversationBorderPane
import no.uib.inf219.example.gui.Styles.Companion.responseHBox
import tornadofx.*

/**
 * @author Elg
 */
class ConversationView(val tab: Tab, var conv: Conversation) : View() {

    override val root = borderpane {
        Main.title.value = conv.name
        addClass(conversationBorderPane)

        setText(this, conv.text)
        bottom {
            hbox {
                addClass(responseHBox)
                conv.hasBeenRead = true
                createButtons(conv.responses, this)
            }
        }
    }

    private fun createButtons(resps: List<Response>, parent: HBox) {
        with(parent) {
            clear()

            for (response in resps) {
                with(button(response.response)) {
                    this.disableProperty().set(!response.prereq.check())
                    setOnAction {
                        if (response.prereq.check()) {
                            if (response.shouldClose()) {
                                tab.close()
                                return@setOnAction
                            }
                            conv = response.conv!!
                            conv.hasBeenRead = true
                            setText(root, conv.text)
                            createButtons(conv.responses, parent)
                        }
                    }
                    tooltip = response.tooltip()
                    addClass(Styles.responseButton)
                }
            }
        }
    }

    private fun setText(parent: BorderPane, text: String) {
        with(parent) {
            top = label(text) {
                addClass(Styles.conversationLabel)
            }
        }
    }
}
