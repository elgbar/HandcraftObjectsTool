package no.uib.inf219.gui.view

import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.*

/**
 * @author Elg
 */
class BackgroundView : View("HOT") {

    override val root = gridpane {

        tabpane {
            ControlPanelView.tabPane = this
            gridpaneConstraints {
                vhGrow = Priority.ALWAYS
            }
            tab("Select Conversation", BorderPane()) {
                add(ControlPanelView)
                this.isClosable = false
            }
        }
    }
}

