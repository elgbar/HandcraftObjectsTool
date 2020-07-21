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

package no.uib.inf219.gui

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import no.uib.inf219.extra.closeAll
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class PaneKtTest {

    @Test
    fun closeTabsCorrectly() {
        with(TabPane()) {
            // non-closable tab
            val nonClosableTab = Tab().also { it.isClosable = false }
            val closableTab1 = Tab()
            val closableTab2 = Tab()
            tabs.add(nonClosableTab)
            // two closable tabs
            tabs.add(closableTab1)
            tabs.add(closableTab2)

            closeAll(false)
            assertTrue(tabs.size == 1 && tabs.contains(nonClosableTab)) {
                "TabPane does not only contain non-closable tab | tabs = $tabs"
            }

            // add the two closable tabs backs back in
            tabs.add(closableTab1)
            tabs.add(closableTab2)

            assertTrue(tabs.containsAll(listOf(nonClosableTab, closableTab1, closableTab2))) {
                "TabPane does not contain all the expected tabs "
            }

            closeAll(true)
            assertTrue(tabs.isEmpty()) {
                "TabPane still contains tabs when force closing all | tabs = $tabs"
            }
        }
    }
}
