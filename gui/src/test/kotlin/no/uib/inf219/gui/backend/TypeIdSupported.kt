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

import com.fasterxml.jackson.annotation.JsonTypeInfo
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.view.ControlPanelView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class TypeIdSupported {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    class UseClassAsPropertyProperty {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY)
    class UseMinimalClassAsPropertyProperty {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    class UseNameAsPropertyProperty {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            return true
        }

        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    @Test
    internal fun useClassAsPropertyPropertyTest() {
        val expected = ControlPanelView.mapper.writeValueAsString(UseClassAsPropertyProperty())
        println("expected = $expected")

        val obj =
            ComplexClassBuilder(
                UseClassAsPropertyProperty::class.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                item = TreeItem()
            ).toObject()
        val json = ControlPanelView.mapper.writeValueAsString(obj)
        println("got = $json")
        assertEquals(UseClassAsPropertyProperty(), obj)
        assertEquals(expected, json)
    }

    @Test
    internal fun useMinimalClassAsPropertyPropertyTest() {
        val expected = ControlPanelView.mapper.writeValueAsString(UseMinimalClassAsPropertyProperty())
        println("expected = $expected")

        val obj =
            ComplexClassBuilder(
                UseMinimalClassAsPropertyProperty::class.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                item = TreeItem()
            ).toObject()
        val json = ControlPanelView.mapper.writeValueAsString(obj)
        println("got = $json")
        assertEquals(UseMinimalClassAsPropertyProperty(), obj)
        assertEquals(expected, json)
    }

    @Test
    internal fun useNameAsPropertyPropertyTest() {
        val expected = ControlPanelView.mapper.writeValueAsString(UseNameAsPropertyProperty())
        println("expected = $expected")

        val obj =
            ComplexClassBuilder(
                UseNameAsPropertyProperty::class.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                item = TreeItem()
            ).toObject()
        val json = ControlPanelView.mapper.writeValueAsString(obj)
        println("got = $json")
        assertEquals(UseNameAsPropertyProperty(), obj)
        assertEquals(expected, json)
    }
}
