package no.uib.inf219.gui

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import no.uib.inf219.gui.extra.closeAll
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
            //non-closable tab
            val nonClosableTab = Tab().also { it.isClosable = false }
            val closableTab1 = Tab()
            val closableTab2 = Tab()
            tabs.add(nonClosableTab)
            //two closable tabs
            tabs.add(closableTab1)
            tabs.add(closableTab2)

            closeAll(false)
            assertTrue(tabs.size == 1 && tabs.contains(nonClosableTab)) {
                "TabPane does not only contain non-closable tab | tabs = $tabs"
            }

            //add the two closable tabs backs back in
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
