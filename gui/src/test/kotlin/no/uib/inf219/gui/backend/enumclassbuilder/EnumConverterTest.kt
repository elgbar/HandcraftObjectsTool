package no.uib.inf219.gui.backend.enumclassbuilder

import no.uib.inf219.extra.FAKE_ROOT
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.simple.EnumClassBuilder
import no.uib.inf219.test.Weather
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class EnumConverterTest {


    lateinit var cb: EnumClassBuilder<Weather>

    @BeforeEach
    internal fun setUp() {
        cb = ClassBuilder.createClassBuilder(
            Weather::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT
        )!! as EnumClassBuilder<Weather>
    }

    //basic check

    @Test
    internal fun fromStringTest() {
        cb.serObject = Weather.SUNNY
        Assertions.assertEquals(Weather.SUNNY, cb.converter.fromString(Weather.SUNNY.name))
    }

    @Test
    internal fun toStringTest() {
        cb.serObject = Weather.RAIN
        Assertions.assertEquals(Weather.RAIN.name, cb.converter.toString(Weather.RAIN))
    }

    //Null checking

    @Test
    internal fun fromString_null() {
        cb.serObject = Weather.SUNNY
        Assertions.assertNull(cb.converter.fromString(null))
    }

    @Test
    internal fun toString_null() {
        cb.serObject = Weather.SUNNY
        Assertions.assertNull(cb.converter.toString(null))
    }

    //invalid arg check

    @Test
    internal fun fromString_invalid() {
        cb.serObject = Weather.SUNNY
        Assertions.assertNull(cb.converter.fromString("something random"))
    }
}
