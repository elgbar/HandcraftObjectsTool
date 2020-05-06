package no.uib.inf219.example.gui.view

import javafx.scene.layout.BorderPane
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane

/**
 * @author Elg
 */
class BackgroundView : View("HOT Conversation Example GUI") {

    override val root = tabpane {
        tabMaxHeight = Double.MAX_VALUE
        tab("Select Conversation", BorderPane()) {
            add(SelectConversationView(this@tabpane))
            this.isClosable = false
        }
    }
}

