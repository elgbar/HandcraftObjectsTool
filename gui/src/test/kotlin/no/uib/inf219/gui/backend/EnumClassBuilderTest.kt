package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.simple.EnumClassBuilder
import no.uib.inf219.test.Weather
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class EnumClassBuilderTest {


    lateinit var cb: EnumClassBuilder<Weather>

    @BeforeEach
    internal fun setUp() {
        cb = ClassBuilder.getClassBuilder(Weather::class.type())!! as EnumClassBuilder<Weather>
    }

    @Test
    internal fun canSerializeEnum() {
        val cb = ClassBuilder.getClassBuilder(Weather::class.type(), value = Weather.SUNNY)
        assertNotNull(cb)

        assertEquals(Weather.SUNNY, cb!!.toObject())
    }

    @Test
    internal fun canSerializeEnum_firstSelectedByName() {
        val cb = ClassBuilder.getClassBuilder(Weather::class.type())
        assertNotNull(cb)

        val defaultSel = Weather.values().apply { sortBy { it.name } }.first()

        assertEquals(defaultSel, cb!!.toObject())
    }

    enum class Weather2 {
        SUNNY,
        CLOUDY,

        @JsonEnumDefaultValue
        RAIN
    }

    @Test
    internal fun canSerializeEnum_firstSelectedByAnnotation() {
        val cb = ClassBuilder.getClassBuilder(Weather2::class.type())
        assertNotNull(cb)

        assertEquals(Weather2.RAIN, cb!!.toObject())
    }

    @Test
    internal fun notImmutable() {
        assertFalse(cb.isImmutable())

        assertDoesNotThrow {
            cb.serObject = Weather.CLOUDY
        }
    }
}
