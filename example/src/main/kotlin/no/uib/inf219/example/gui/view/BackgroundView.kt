package no.uib.inf219.example.gui.view

import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import no.uib.inf219.example.gui.Main
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
                this.isClosable = false
            }
            tab("Example Conversation #1", BorderPane()) {
                add(ConversationView(this, Main.TEST_CONV).root)
            }
        }
    }
}
