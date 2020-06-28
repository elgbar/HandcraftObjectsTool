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

package no.uib.inf219.gui.backend.cb.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import kotlin.reflect.KClass

/**
 * @author Elg
 */
abstract class AbstractClassBuilderSerializer<T : ClassBuilder>(clazz: KClass<T>) : StdSerializer<T>(clazz.type()) {

    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        serializeMaybeWithType(value, gen, provider, null)
    }

    override fun serializeWithType(
        value: T,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer
    ) {
        serializeMaybeWithType(value, gen, provider, typeSer)
    }

    /**
     * Serialize the object, but it is unknown if [typeSer] will be present.
     *
     * @param cb Class Builder to serialize
     * @param gen Generator used to output resulting Json content
     * @param provider Provider that can be used to get serializers for
     *   serializing Objects value contains, if any
     * @param typeSer Type serializer to use for including type information, if any
     *
     * @see serialize
     * @see serializeWithType
     */
    abstract fun serializeMaybeWithType(
        cb: T,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    )
}
