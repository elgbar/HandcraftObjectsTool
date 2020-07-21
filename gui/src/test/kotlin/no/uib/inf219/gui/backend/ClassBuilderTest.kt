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

import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderTest {

    @Test
    internal fun getClassBuilder_failOnTypeMismatch() {
        assertThrows(IllegalArgumentException::class.java) {
            createClassBuilder(
                String::class.type(), key = "key".toCb(), parent = FAKE_ROOT, value = 2
            )
        }
    }

    @Test
    internal fun getClassBuilder_worksForPrimitives() {
        assertDoesNotThrow {
            createClassBuilder(
                Boolean::class.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                value = true
            )
        }

        assertDoesNotThrow {
            createClassBuilder(
                Boolean::class.javaPrimitiveType!!.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                value = true
            )
        }
    }
}
