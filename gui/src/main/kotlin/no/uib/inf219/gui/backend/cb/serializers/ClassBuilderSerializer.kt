package no.uib.inf219.gui.backend.cb.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import no.uib.inf219.extra.type
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
        //find the real serializer and delegate to it
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

