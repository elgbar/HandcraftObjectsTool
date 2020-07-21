/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
