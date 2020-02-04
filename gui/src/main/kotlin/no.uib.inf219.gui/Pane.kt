package no.uib.inf219.gui

import javafx.scene.control.TabPane
import tornadofx.close

/**
 * @author Elg
 */
fun TabPane.closeAllTabs() {
    for (tab in tabs) {
        if (tab.isClosable)
            tab.close()
    }
}
