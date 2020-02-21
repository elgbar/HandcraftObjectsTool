package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import no.uib.inf219.gui.ems
import tornadofx.*

/**
 * @author Elg
 */
class BackgroundView : View("HOT") {

    val tabpane: TabPane

    override val root = splitpane(orientation = Orientation.VERTICAL)

    init {
        with(root) {
            style {
                minWidth = 110.ems
                minHeight = 60.ems
            }
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
