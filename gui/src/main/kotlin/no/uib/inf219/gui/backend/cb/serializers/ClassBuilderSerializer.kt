package no.uib.inf219.gui.backend.cb.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import no.uib.inf219.gui.backend.cb.api.ClassBuilder

/**
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
        val ser: JsonSerializer<Any> = provider.findValueSerializer(cb.type)
        ser.serialize(cb.serObject, gen, provider)
    }

}

