package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderTest {

    @Test
    internal fun isParent_self() {
        val parent = CollectionClassBuilder<Any>(ArrayList::class.type() as CollectionLikeType, "list")
        val child = "test123 :)".toCb(parent = parent, immutable = false)

        assertFalse(child.isParentOf(child))
        assertFalse(parent.isParentOf(parent))
    }

    @Test
    internal fun isParent_direct() {
        val parent = CollectionClassBuilder<Any>(ArrayList::class.type() as CollectionLikeType, "list")
        val child = "test123 :)".toCb(parent = parent, immutable = false)

        assertTrue(parent.isParentOf(child))
        assertFalse(child.isParentOf(parent))
    }

    @Test
    internal fun isParent_grandChild() {
        val parent = CollectionClassBuilder<Any>(ArrayList::class.type() as CollectionLikeType, "list")
        val child = CollectionClassBuilder<Any>(ArrayList::class.type() as CollectionLikeType, "list", parent)
        val grandChild = "test123 :)".toCb(parent = child, immutable = false)

        assertTrue(parent.isParentOf(child))
        assertTrue(child.isParentOf(grandChild))

        //make sure this is transient
        assertTrue(parent.isParentOf(grandChild))
    }
}
