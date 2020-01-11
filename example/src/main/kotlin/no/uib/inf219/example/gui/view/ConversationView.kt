package no.uib.inf219.example.gui.view

import javafx.scene.control.ButtonBar
import javafx.scene.layout.HBox
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.gui.controller.ViewController
import tornadofx.*

/**
 * @author Elg
 */
class ConversationView(val cont: ViewController) : View() {
    override val root = borderpane {
        title = "conversation"
        top = label(cont.conv.text)

        center {
            hbox {
                createButtons(cont.conv.responses, this)
            }
        }
    }

    private fun createButtons(resps: List<Response>, parent: HBox) {
        with(parent) {
            clear()
            buttonbar {
                for (response in resps) {
                    button(response.text, type = ButtonBar.ButtonData.NEXT_FORWARD).setOnAction {
                        response.onSelect()
                        cont.conv = response.conversation
                        root.top = label(cont.conv.text)
                        createButtons(response.conversation.responses, parent)
                    }
                }
            }
        }
    }
}
