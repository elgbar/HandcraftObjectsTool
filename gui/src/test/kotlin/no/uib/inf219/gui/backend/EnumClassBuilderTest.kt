package no.uib.inf219.gui.backend

import no.uib.inf219.extra.type
import no.uib.inf219.test.Weather
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class EnumClassBuilderTest {

    @Test
    internal fun canSerializeEnum() {
        val cb = ClassBuilder.getClassBuilder(Weather::class.type(), "weather", value = Weather.SUNNY)
        assertNotNull(cb)

        assertEquals(Weather.SUNNY, cb!!.toObject())
    }

    @Test
    internal fun canSerializeEnum_nullValue() {
        val cb = ClassBuilder.getClassBuilder(Weather::class.type(), "weather")
        assertNotNull(cb)

        assertEquals(Weather.values()[0], cb!!.toObject())
    }

    @Test
    internal fun notImmutable() {
        val cb = EnumClassBuilder(Weather::class.java, Weather.SUNNY, "name")
        assertFalse(cb.isImmutable())
        assertFalse(cb.immutable)

        assertDoesNotThrow {
            cb.serObject = Weather.CLOUDY
        }
    }
}
