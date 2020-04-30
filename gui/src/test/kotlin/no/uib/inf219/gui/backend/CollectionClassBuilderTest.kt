package no.uib.inf219.gui.backend

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.parents.CollectionClassBuilder
import no.uib.inf219.gui.backend.cb.simple.StringClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
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

    @Test
    internal fun serialization_twoElems() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        assertTrue(parent.serObject.isEmpty())

        val child =
            parent.createChildClassBuilder(0.toCb(immutable = false), item = TreeItem()) as StringClassBuilder
        child.serObject = "hello"

        val child2 =
            parent.createChildClassBuilder(1.toCb(immutable = false), item = TreeItem()) as StringClassBuilder
        child2.serObject = "world!"

        assertEquals(listOf("hello", "world!"), parent.toObject())
    }

    @Test
    internal fun serialization_oneElem() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        assertTrue(parent.serObject.isEmpty())

        val child =
            parent.createChildClassBuilder(0.toCb(immutable = false), item = TreeItem()) as StringClassBuilder
        child.serObject = "hello!"

        assertEquals(listOf("hello!"), parent.toObject())
    }

    @Test
    internal fun serialization_empty() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder
        assertEquals(emptyList<Any>(), parent.toObject())
    }
}
