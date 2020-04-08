package no.uib.inf219.extra

import javafx.event.Event
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
        if (force || tab.isClosable) {
            Event.fireEvent(tab, Event(Tab.CLOSED_EVENT))
            iter.remove() // same as 'tab.close()'
        }
    }
}

fun Tab.close() {
    Event.fireEvent(this, Event(Tab.CLOSED_EVENT))
    tabPane?.tabs?.remove(this)
}

