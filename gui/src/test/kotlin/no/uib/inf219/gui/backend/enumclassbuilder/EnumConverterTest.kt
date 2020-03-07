package no.uib.inf219.gui.backend.enumclassbuilder

import no.uib.inf219.gui.backend.EnumClassBuilder
import no.uib.inf219.test.Weather
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class EnumConverterTest {

    //basic check

    @Test
    internal fun fromStringTest() {
        val cb = EnumClassBuilder(Weather::class.java, Weather.SUNNY, "name")
        Assertions.assertEquals(Weather.SUNNY, cb.converter.fromString(Weather.SUNNY.name))
    }

    @Test
    internal fun toStringTest() {
        val cb = EnumClassBuilder(Weather::class.java, Weather.SUNNY, "name")
        Assertions.assertEquals(Weather.SUNNY.name, cb.converter.toString(Weather.SUNNY))
    }

    //Null checking

    @Test
    internal fun fromString_null() {
        val cb = EnumClassBuilder(Weather::class.java, Weather.SUNNY, "name")
        Assertions.assertNull(cb.converter.fromString(null))
    }

    @Test
    internal fun toString_null() {
        val cb = EnumClassBuilder(Weather::class.java, Weather.SUNNY, "name")
        Assertions.assertNull(cb.converter.toString(null))
    }

    //invalid arg check

    @Test
    internal fun fromString_invalid() {
        val cb = EnumClassBuilder(Weather::class.java, Weather.SUNNY, "name")
        Assertions.assertNull(cb.converter.fromString("something random"))
    }
}
