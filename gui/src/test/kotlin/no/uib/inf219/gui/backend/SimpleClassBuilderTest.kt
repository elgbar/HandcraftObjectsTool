package no.uib.inf219.gui.backend

import javafx.beans.property.*
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class SimpleClassBuilderTest {

    private fun createCB(): SimpleClassBuilder<String> {
        return INIT_VAL.toCb(immutable = false)
    }

    companion object {
        const val INIT_VAL = "This is a string"
    }

    @Test
    fun reset() {
        val cb = createCB()
        assertEquals(cb.serializationObject, INIT_VAL)
        cb.serializationObject = INIT_VAL + "test"
        assertNotEquals(cb.serializationObject, INIT_VAL)
        assertEquals(false, cb.reset())
        assertEquals(cb.serializationObject, INIT_VAL)
    }

    @Test
    fun isLeaf() {
        assertTrue(createCB().isLeaf())
    }

    @Test
    internal fun editingImmutableCBThrows() {

        //no need to specify that the cb is immutable, other than to make it future proof
        val cb = "test".toCb(immutable = true)

        assertThrows(IllegalStateException::class.java) {
            cb.serializationObject = "not allowed"
        }
    }

    @Test
    internal fun editingMutableCBAllowed() {
        val cb = "test".toCb(immutable = false)

        assertDoesNotThrow {
            cb.serializationObject = "Allowed"
        }
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
