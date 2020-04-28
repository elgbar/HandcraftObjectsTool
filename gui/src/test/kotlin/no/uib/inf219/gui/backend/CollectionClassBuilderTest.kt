package no.uib.inf219.gui.backend

import javafx.scene.control.TreeItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class CollectionClassBuilderTest {

    companion object {
        val listStrType
            get() = ControlPanelView.mapper.typeFactory.constructCollectionType(List::class.java, String::class.java)
    }

    @Test
    internal fun resetChildWorks() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        assertTrue(parent.serObject.isEmpty())

        val child = parent.createChildClassBuilder(0.toCb(immutable = false), item = TreeItem())
        assertNotNull(child)
        assertEquals(1, parent.serObject.size)
        assertTrue(parent.serObject.contains(child))

        parent.resetChild(0.toCb(), restoreDefault = false)

        assertTrue(parent.serObject.isEmpty())
        assertFalse(parent.serObject.contains(child))
    }
}
