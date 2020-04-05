package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderTest {

    fun listCB(parent: ParentClassBuilder = SimpleClassBuilder.FAKE_ROOT): CollectionClassBuilder {
        return CollectionClassBuilder(
            ArrayList::class.type() as CollectionLikeType,
            key = "key".toCb(),
            parent = parent,
            item = TreeItem()
        )
    }

    @Test
    internal fun isParent_self() {
        val parent = listCB()
        val child = listCB(parent)

        assertFalse(parent.isParentOf(parent))
        assertFalse(child.isParentOf(parent))
    }

    @Test
    internal fun isParent_sibling() {
        val parent = listCB()
        val childA = listCB(parent)
        val childB = listCB(parent)

        assertFalse(childA.isParentOf(childB))
        assertFalse(childB.isParentOf(childA))
    }

    @Test
    internal fun isParent_direct() {
        val parent = listCB()
        val child = listCB(parent)

        assertTrue(parent.isParentOf(child))
        assertFalse(child.isParentOf(parent))
    }

    @Test
    internal fun isParent_grandChild() {
        val parent = listCB()
        val child = listCB(parent)
        val grandChild = listCB(child)

        assertTrue(parent.isParentOf(child))
        assertTrue(child.isParentOf(grandChild))

        //make sure this is transitive
        assertTrue(parent.isParentOf(grandChild))

        //sanity check
        assertFalse(grandChild.isParentOf(parent))
    }

    @Test
    internal fun getClassBuilder_failOnTypeMismatch() {
        assertThrows(IllegalArgumentException::class.java) {
            ClassBuilder.createClassBuilder(
                String::class.type(), value = 2, key = "key".toCb(), parent = SimpleClassBuilder.FAKE_ROOT
            )
        }
    }

    @Test
    internal fun getClassBuilder_worksForPrimitives() {
        assertDoesNotThrow {
            ClassBuilder.createClassBuilder(
                Boolean::class.type(),
                value = true,
                key = "key".toCb(),
                parent = SimpleClassBuilder.FAKE_ROOT
            )
        }

        assertDoesNotThrow {
            ClassBuilder.createClassBuilder(
                Boolean::class.javaPrimitiveType!!.type(),
                value = true,
                key = "key".toCb(),
                parent = SimpleClassBuilder.FAKE_ROOT
            )
        }
    }

    @Disabled
    @Test
    internal fun resetChild_dontRestore_collection() {
        TODO("not implemented")
    }

    @Disabled
    @Test
    internal fun resetChild_dontRestore_map() {
        TODO("not implemented")
    }
}
