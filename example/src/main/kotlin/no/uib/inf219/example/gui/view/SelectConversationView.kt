package no.uib.inf219.example.gui.view

import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import no.uib.inf219.example.gui.Main
import tornadofx.*


/**
 * @author Elg
 */
class SelectConversationView(val tabPane: TabPane) : View("") {

    override val root = gridpane {
        vbox {
            style {
                padding = box(0.5.em)
            }
            button {
                text = "Create hardcoded conversation"
                setOnAction {
                    tabPane.tab("Hardcoded conversation", BorderPane()) {
                        add(ConversationView(this, Main.TEST_CONV).root)
                        tabPane.selectionModel.select(this)
                    }
                }
            }
        }
    }
}
