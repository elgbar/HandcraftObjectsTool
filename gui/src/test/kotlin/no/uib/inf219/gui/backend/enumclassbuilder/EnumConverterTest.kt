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

package no.uib.inf219.gui.backend.enumclassbuilder

import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.simple.EnumClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
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
        cb = createClassBuilder(
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
