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
        //find the real serializer and delegate to it
        //
        //Do not use the [cb.type] variable as it may represent an interface or abstract class!s
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

