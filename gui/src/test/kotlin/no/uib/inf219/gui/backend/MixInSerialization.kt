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

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.view.ControlPanelView.mapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class MixInSerialization {

    open class UnmodifiableClass {
        private var hidden: String? = "hoho"

        init {
            hidden = "hoho"
        }

        override fun equals(other: Any?): Boolean {
            if (other !is UnmodifiableClass) return false
            if (hidden != other.hidden) return false
            return true
        }

        override fun toString(): String {
            return "UnmodifiableClass(hidden=$hidden)"
        }
    }

    class UnmodifiableChildClass : UnmodifiableClass() {

        @JsonProperty("wo")
        private var anotherHidden: String? = null
    }

    class UnmodifiableClassMixIn {
        companion object {
            const val KEY = "no-longer-hidden"
        }

        @JsonProperty(KEY)
        var hidden: String? = null
    }

    @AfterEach
    internal fun tearDown() {
        mapper = ObjectMapper()
    }

    @Test
    internal fun canSerializeMixInAnnotation() {

        KotlinModule

//        mapper.addMixIn(UnmodifiableClass::class.java, UnmodifiableClassMixIn::class.java)

        val inst = UnmodifiableClass()
        mapper = ObjectMapper().registerModule(object : SimpleModule() {
            init {
                setMixInAnnotation(UnmodifiableClass::class.java, UnmodifiableClassMixIn::class.java)
            }
        })
        val expectedJson = mapper.writeValueAsString(inst)
        println(expectedJson)

        val cb = ComplexClassBuilder(
            UnmodifiableClass::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT,
            item = TreeItem()
        )
        assertEquals(UnmodifiableClassMixIn.KEY, cb.propInfo.keys.first())
        cb.serObject[UnmodifiableClassMixIn.KEY] = "hoho".toCb()

        println("serialized=" + mapper.writeValueAsString(cb))

        val obj = cb.toObject()

        assertEquals(inst, obj)
    }

    @Test
    internal fun canSerializeChildOfMixInAnnotation() {

//        mapper.addMixIn(UnmodifiableClass::class.java, UnmodifiableClassMixIn::class.java)

        mapper = ObjectMapper().registerModule(object : SimpleModule() {
            init {
                setMixInAnnotation(UnmodifiableClass::class.java, UnmodifiableClassMixIn::class.java)
            }
        })

        val inst = UnmodifiableChildClass()
        val expectedJson = mapper.writeValueAsString(inst)
        println(expectedJson)

        val cb = ComplexClassBuilder(
            UnmodifiableChildClass::class.type(),
            key = "key".toCb(),
            parent = FAKE_ROOT,
            item = TreeItem()
        )
//        assertEquals(UnmodifiableClassMixIn.KEY, cb.propInfo.keys.first())
        cb.serObject[UnmodifiableClassMixIn.KEY] = "hoho".toCb()

        println("serialized=" + mapper.writeValueAsString(cb))

        val obj = cb.toObject()

        assertEquals(inst, obj)
    }
}
