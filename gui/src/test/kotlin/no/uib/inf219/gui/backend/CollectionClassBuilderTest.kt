package no.uib.inf219.gui.backend

import javafx.scene.control.TreeItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.parents.CollectionClassBuilder
import no.uib.inf219.gui.backend.cb.simple.StringClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
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

        val setStrType
            get() = ControlPanelView.mapper.typeFactory.constructCollectionType(Set::class.java, String::class.java)
    }

    @Test
    internal fun resetChildWorks() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        assertTrue(parent.serObject.isEmpty())

        val child = parent.createChild(0.toCb(immutable = false), item = TreeItem())
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
            parent.createChild(0.toCb(immutable = false), item = TreeItem()) as StringClassBuilder
        child.serObject = "hello"

        val child2 =
            parent.createChild(1.toCb(immutable = false), item = TreeItem()) as StringClassBuilder
        child2.serObject = "world!"

        assertEquals(listOf("hello", "world!"), parent.toObject())
    }

    @Test
    internal fun serialization_oneElem() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        assertTrue(parent.serObject.isEmpty())

        val child =
            parent.createChild(0.toCb(immutable = false), item = TreeItem()) as StringClassBuilder
        child.serObject = "hello!"

        assertEquals(listOf("hello!"), parent.toObject())
    }

    @Test
    internal fun serialization_empty() {
        val parent = ObjectEditorController(listStrType).root
        assertEquals(emptyList<Any>(), parent.toObject())
    }


    @Test
    internal fun serialization_setAtCollSize_nonNull() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder
        val ckey = CollectionClassBuilder.createChildKey(0)

        val child = "hellO!".toCb(ckey, parent, parent.getChildPropertyMetadata(ckey), item = TreeItem())
        child.item.value = FilledClassBuilderNode(ckey, child, parent)
        parent[ckey] = child

        assertEquals(listOf("hellO!"), parent.toObject())
    }


    @Test
    internal fun canLoadSerialized_Collection_SizeZero() {
        val parent = ObjectEditorController(listStrType, emptyList<String>()).root
        assertNotNull(parent)

        assertEquals(emptyList<String>(), parent.toObject())
    }

    @Test
    internal fun canLoadSerialized_List_SizeOne() {
        val real = listOf("Hello")

        val parent = ObjectEditorController(listStrType, real).root
        assertNotNull(parent)
        assertEquals(real, parent.toObject())
    }

    @Test
    internal fun canLoadSerialized_List_SizeN() {
        val real = listOf("Hello", "world", "world")

        val parent = ObjectEditorController(listStrType, real).root
        assertNotNull(parent)
        assertEquals(real, parent.toObject())
    }

    @Test
    internal fun canLoadSerialized_Set_SizeN() {
        val real = setOf("Hello", "world", "world")

        val parent = ObjectEditorController(setStrType, real).root
        assertNotNull(parent)
        assertEquals(real, parent.toObject())
    }

    @Test
    internal fun canLoadSerialized_ArrayOfStrings_SizeN() {
        val real = arrayOf("Hello", "world", "world")

        val parent = ObjectEditorController(real.javaClass.type().withContentType(String::class.type()), real).root
        assertNotNull(parent)
        assertArrayEquals(real, parent.toObject() as Array<*>)
    }

    @Test
    internal fun canLoadSerialize_IntArray_SizeN() {
        val real = intArrayOf(4, -1, 2)

        val parent = ObjectEditorController(IntArray::class.type(), real).root
        assertNotNull(parent)
        //we're always casting t
        assertArrayEquals(real.toTypedArray(), parent.toObject() as Array<Int>)
    }

    @Test
    internal fun canLoadSerialize_ArrayOfInts_SizeN() {
        val real = arrayOf(4, -1, 2)

        val parent = ObjectEditorController(real::class.type(), real).root
        assertNotNull(parent)
        assertArrayEquals(real, parent.toObject() as Array<*>)
    }
}
