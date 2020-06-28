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

import javafx.scene.control.TreeItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.test.UselessRecursiveObject
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class RecursiveClassBuilderTest {

    @Test
    internal fun canSerialiseRecursiveClass() {
        val rec = UselessRecursiveObject()
        rec.with = rec

        var json: String? = null
        assertDoesNotThrow {
            json = ControlPanelView.mapper.writeValueAsString(rec)
        }
        assertEquals("{\"@id\":1,\"with\":1}", json)
    }

    @Test
    internal fun canDeserializeRecursiveClass() {
        val json = "{\"@id\":1,\"with\":1}"

        val made: UselessRecursiveObject
        try {
            made = ControlPanelView.mapper.readValue(json, UselessRecursiveObject::class.java)
        } catch (e: Throwable) {
            fail<Any>("Could not read from json", e)
            return
        }

        val rec = UselessRecursiveObject()
        rec.with = rec

        assertTrue(made === made.with)
    }

    @Test
    internal fun canCreateRecursiveClass() {

        val cb = ComplexClassBuilder(
            UselessRecursiveObject::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT,
            item = TreeItem()
        )

        //do not use ReferenceClassBuilder here as it will create a cycle with itself, and not the parent
        cb.serObject[UselessRecursiveObject::with.name] = cb

        var created: UselessRecursiveObject? = null
        assertDoesNotThrow {
            created = cb.toObject() as UselessRecursiveObject?
            println(ControlPanelView.mapper.writeValueAsString(cb.serObject))
        }


        assertNotNull(created)
        assertTrue(created === created!!.with)

        assertEquals("{\"@id\":1,\"with\":1}", ControlPanelView.mapper.writeValueAsString(created))

    }
}
