package no.uib.inf219.gui.converter

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

/**
 * @author Elg
 */
internal class UUIDStringConverterTest {

    @Test
    fun testToString() {
        assertNull(UUIDStringConverter.toString(null))
        assertNotNull(UUIDStringConverter.toString(UUID.randomUUID()))
    }

    @Test
    fun fromString() {
        assertNull(UUIDStringConverter.fromString(null))
        assertNotNull(UUIDStringConverter.fromString(UUID.randomUUID().toString()))
    }
}
