package no.uib.inf219.example.gui.view

import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * @author Elg
 */
class BackgroundView : View("HOT Conversation Example GUI") {


    override val root = gridpane() {
        setPrefSize(600.0, 480.0)
//        center = ConversationView(Main.TEST_CONV).root

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
