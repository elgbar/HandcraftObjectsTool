package no.uib.inf219.extra

import javafx.scene.control.Tab
import javafx.scene.control.TabPane

/**
 * Close all closable tabs
 *
 * @param force If tabs with [Tab.isClosable] set to `false` should also be closed
 *
 * @author Elg
 */
fun TabPane.closeAll(force: Boolean = false) {
    val iter = this.tabs.iterator()
    while (iter.hasNext()) {
        val tab = iter.next()
        if (force || tab.isClosable)
            iter.remove() // same as 'tab.close()'
    }
}

