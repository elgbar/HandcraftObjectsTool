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
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.ClassBuilder

/**
 * Serialize class builders where the [ClassBuilder.serObject] can be serialized without any extra configuration
 *
 * @author Elg
 */
object ClassBuilderSerializer : AbstractClassBuilderSerializer<ClassBuilder>(
    ClassBuilder::class
) {

    override fun serializeMaybeWithType(
        cb: ClassBuilder,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {
        // find the real serializer and delegate to it
        //
        // Do not use the [cb.type] variable as it may represent an interface or abstract class!s
        // The serializer is for the sub class, not super class
        val ser: JsonSerializer<Any> = provider.findValueSerializer(cb.serObject::class.type())
            ?: error("Failed to find serializer for ${cb.serObject}")

        if (cb.serObject === cb) {
            error("Endless cycle detected. class builder is referencing it self")
        }

        if (typeSer != null)
            ser.serializeWithType(cb.serObject, gen, provider, typeSer)
        else
            ser.serialize(cb.serObject, gen, provider)
    }
}
