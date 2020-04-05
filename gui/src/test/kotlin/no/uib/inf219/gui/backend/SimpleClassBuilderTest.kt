package no.uib.inf219.gui.backend

import javafx.beans.property.*
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension


/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class SimpleClassBuilderTest {

    private fun createCB(): SimpleClassBuilder<*> {
        return INIT_VAL.toCb(immutable = false)
    }

    companion object {
        const val INIT_VAL = "This is a string"
    }

    @Test
    fun isLeaf() {
        assertTrue(createCB().isLeaf())
    }

    @Test
    internal fun editingMutableCBAllowed() {
        val cb = "test".toCb(immutable = false)
        val newValue = "Allowed"
        cb.serObject = newValue
        assertEquals(cb.serObject, newValue)
    }

    @Test
    internal fun findProps() {
        assertTrue(SimpleClassBuilder.findProperty(Int::class.type(), 0) is SimpleIntegerProperty)
        assertTrue(SimpleClassBuilder.findProperty(Long::class.type(), 0L) is SimpleLongProperty)
        assertTrue(SimpleClassBuilder.findProperty(Double::class.type(), 0.0) is SimpleDoubleProperty)
        assertTrue(SimpleClassBuilder.findProperty(Float::class.type(), 0f) is SimpleFloatProperty)
        assertTrue(SimpleClassBuilder.findProperty(Boolean::class.type(), true) is SimpleBooleanProperty)
        assertTrue(SimpleClassBuilder.findProperty(String::class.type(), INIT_VAL) is SimpleStringProperty)
    }
}
