package no.uib.inf219.example.gui.view

import no.uib.inf219.example.gui.controller.ViewController
import tornadofx.*

/**
 * @author Elg
 */
class ConversationView(cont: ViewController) : View() {
    override val root = borderpane {
        title = "conversation ${cont.conv.name}"
        top = label(cont.conv.text)

        center {
            //            hbox {
//                alignment = Pos.CENTER
//                for (response in conv.responses) {
//                    ResponseView(response)
//                }
//            }
            hbox {

                buttonbar {
                    for (response in cont.conv.responses) {
//                        ResponseView(response)
                        button(response.text).setOnAction {
                            cont.conv = response.conversation
                        }
                    }
                    button("Exit").setOnAction { close() }
                }
            }
        }
    }
}
