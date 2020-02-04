package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import tornadofx.*

/**
 * @author Elg
 */
class BackgroundView : View("HOT") {

    val tabpane: TabPane

    override val root = splitpane(orientation = Orientation.VERTICAL)

    init {
        with(root) {
            setDividerPositions(0.75)
            tabpane = tabpane {
                tab("Select Conversation", BorderPane()) {
                    add(ControlPanelView)
                    this.isClosable = false
                }
            }
            this += OutputArea
        }
    }
}
