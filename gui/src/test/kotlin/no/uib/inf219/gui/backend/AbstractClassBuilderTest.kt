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
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.test.GenericExample
import no.uib.inf219.test.GenericExampleWithAbstractDefault
import no.uib.inf219.test.precondition.AlwaysFalsePrecondition
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import no.uib.inf219.test.precondition.Precondition
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
class AbstractClassBuilderTest {
    @Test
    internal fun allowLoadingOfAbstractTypes_WhenSerializingItDirectly() {
        val real = AlwaysTruePrecondition()
        val cb = ObjectEditorController(Precondition::class.type(), real).root
        val cbObj = cb.toObject()
        assertEquals(real, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenField() {
        val real = GenericExample(AlwaysTruePrecondition())
        val cb = ObjectEditorController(real::class.type(), real).root
        val cbObj = cb.toObject()
        assertEquals(real, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenInArray() {
        val real = arrayOf(AlwaysTruePrecondition(), AlwaysFalsePrecondition())
        val type = mapper.typeFactory.constructArrayType(Precondition::class.java)
        val cb = ObjectEditorController(type, real).root
        val cbObj = cb.toObject() as Array<*>
        assertArrayEquals(real, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenDefaultInField() {
        val expected = GenericExampleWithAbstractDefault(AlwaysFalsePrecondition())
        val cb = ObjectEditorController(GenericExampleWithAbstractDefault::class.type()).root
        val cbObj = cb.toObject()
        assertEquals(expected, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenInMap_value() {
        val real = mapOf(true to AlwaysTruePrecondition())
        val type = mapper.typeFactory.constructMapType(
            Map::class.java,
            Boolean::class.java,
            Precondition::class.java
        )
        val cb = mapper.readValue<Any>(
            mapper.writeValueAsString(
                ObjectEditorController(
                    type,
                    real
                ).root.toObject()
            ),
            type
        )
        assertEquals(real, cb)
    }
}
