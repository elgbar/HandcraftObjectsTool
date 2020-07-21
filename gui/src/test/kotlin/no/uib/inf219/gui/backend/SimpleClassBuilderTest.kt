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

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.beans.property.SimpleStringProperty
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.test.Weather
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import java.util.UUID

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class SimpleClassBuilderTest {

    private fun createCB(): SimpleClassBuilder<*> {
        return INIT_VAL.toCb(immutable = false)
    }

    companion object {
        const val INIT_VAL = "This is a string"
    }

    @Test
    fun isLeaf() {
        assertTrue(createCB().isLeaf())
    }

    @Test
    internal fun editingMutableCBAllowed() {
        val cb = "test".toCb(immutable = false)
        val newValue = "Allowed"
        cb.serObject = newValue
        assertEquals(cb.serObject, newValue)
    }

    @Test
    internal fun objectPrimitivesBehavesAsPrimitives() {
        val objInt = ObjectEditorController(Int::class.javaObjectType.type()).root
        val primInt = 0.toCb()
        assertEquals(primInt::class, objInt::class)
    }

    @Test
    internal fun findProps() {
        assertTrue(
            SimpleClassBuilder.findProperty(
                Int::class.type(),
                0
            ) is SimpleIntegerProperty
        ) { "It is ${SimpleClassBuilder.findProperty(Int::class.type(), 0)}" }
        assertTrue(SimpleClassBuilder.findProperty(Long::class.type(), 0L) is SimpleLongProperty)
        assertTrue(SimpleClassBuilder.findProperty(Double::class.type(), 0.0) is SimpleDoubleProperty)
        assertTrue(SimpleClassBuilder.findProperty(Float::class.type(), 0f) is SimpleFloatProperty)
        assertTrue(SimpleClassBuilder.findProperty(Boolean::class.type(), true) is SimpleBooleanProperty)
        assertTrue(SimpleClassBuilder.findProperty(String::class.type(), INIT_VAL) is SimpleStringProperty)
    }

    @Test
    internal fun simpleCB_allowForInitialValue_byte() {
        val init = 1.toByte()
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_short() {
        val init = 1.toShort()
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_int() {
        val init = 1
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_long() {
        val init = 1L
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_double() {
        val init = 1.0
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_float() {
        val init = 1f
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_boolean() {
        val init = false
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_char() {
        val init = 'a'
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_uuid() {
        val init: UUID = UUID.randomUUID()
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_enum() {
        val init = Weather.values()[1]
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }

    @Test
    internal fun simpleCB_allowForInitialValue_String() {
        val init = "strng!"
        val cb = ObjectEditorController(init::class.type(), init).root
        assertEquals(init, cb.toObject())
    }
}
