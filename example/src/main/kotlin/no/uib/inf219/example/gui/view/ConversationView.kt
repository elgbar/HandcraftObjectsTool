package no.uib.inf219.example.gui.view

import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
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
        setText(this, cont.conv.text)
        bottom {
            hbox {
                alignment = Pos.BASELINE_LEFT
                createButtons(cont.conv.responses, this)
            }
        }
    }

    private fun createButtons(resps: List<Response>, parent: HBox) {
        with(parent) {
            clear()

            for (response in resps) {
                add(response)

                response.setOnAction {
                    response.onSelect()
                    cont.conv = response.conv
                    setText(root, cont.conv.text)
                    createButtons(response.conv.responses, parent)
                }
                response.tooltip = response.tooltip()

            }
        }
    }

    private fun setText(parent: BorderPane, text: String) {
        with(parent) {
            top = borderpane {
                center = label(text)
            }
        }
    }
}
