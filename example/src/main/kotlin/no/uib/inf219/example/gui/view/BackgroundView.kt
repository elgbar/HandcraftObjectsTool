package no.uib.inf219.example.gui.view

import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * @author Elg
 */
class BackgroundView : View("HOT Conversation Example GUI") {


    override val root = gridpane() {

        tabpane {
            gridpaneConstraints {
                vhGrow = Priority.ALWAYS
            }
            tab("Select Conversation", BorderPane()) {
                add(SelectConversationView(this@tabpane))
                this.isClosable = false
            }
        }
    }
}
