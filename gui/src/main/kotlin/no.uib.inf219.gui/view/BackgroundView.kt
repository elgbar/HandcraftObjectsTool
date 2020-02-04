package no.uib.inf219.gui.view

import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.gridpaneConstraints
import tornadofx.tab
import tornadofx.tabpane

/**
 * @author Elg
 */
class BackgroundView : View("HOT") {

    override val root: TabPane = tabpane {
        
        gridpaneConstraints {
            vhGrow = Priority.ALWAYS
        }
        tab("Select Conversation", BorderPane()) {
            add(ControlPanelView)
            this.isClosable = false
        }

    }
}
