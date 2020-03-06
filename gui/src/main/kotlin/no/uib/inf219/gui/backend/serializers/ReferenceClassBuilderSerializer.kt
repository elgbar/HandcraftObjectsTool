package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ReferenceClassBuilder

/**
 * @author Elg
 */
object ReferenceClassBuilderSerializer : StdSerializer<ReferenceClassBuilder>(ReferenceClassBuilder::class.type()) {

    override fun serialize(value: ReferenceClassBuilder, gen: JsonGenerator, provider: SerializerProvider) {
        deligateToRealSerializer(value, gen, provider, null)
    }

    override fun serializeWithType(
        value: ReferenceClassBuilder,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {
        deligateToRealSerializer(value, gen, provider, typeSer)
    }

    private fun deligateToRealSerializer(
        value: ReferenceClassBuilder,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {
        //find the real serializer and delegate to it
        val ser: JsonSerializer<Any> = provider.findValueSerializer(value.serObject::class.type())
            ?: error("Failed to find serializer for ${value.serObject}")
        if (typeSer != null)
            ser.serializeWithType(value.serObject, gen, provider, typeSer)
        else
            ser.serialize(value.serObject, gen, provider)
    }
}
