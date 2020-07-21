/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.simple.EnumClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.test.Weather
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
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
        @Suppress("UNCHECKED_CAST")
        cb = createClassBuilder(
            Weather::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT
        ) as EnumClassBuilder<Weather>
    }

    @Test
    internal fun canSerializeEnum() {
        val cb = createClassBuilder(
            Weather::class.type(), key = "key".toCb(),
            parent = FAKE_ROOT,
            value = Weather.SUNNY
        )
        assertNotNull(cb)

        assertEquals(Weather.SUNNY, cb!!.toObject())
    }

    @Test
    internal fun canSerializeEnum_firstSelectedByName() {
        val cb = createClassBuilder(
            Weather::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT
        )
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
        val cb = createClassBuilder(
            Weather2::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT
        )
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
