package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import no.uib.inf219.gui.backend.MapClassBuilder

/**
 * @author Elg
 */
object MapClassBuilderSerializer : AbstractClassBuilderSerializer<MapClassBuilder>(MapClassBuilder::class) {

    override fun serializeMaybeWithType(
        cb: MapClassBuilder,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {
        val map = cb.serObject.map {
            val key = it.serObject[MapClassBuilder.ENTRY_KEY]
            val value = it.serObject[MapClassBuilder.ENTRY_VALUE]
            key to value
        }.toMap()

        //find the real serializer and delegate to it
        val ser =
            provider.findValueSerializer(HashMap::class.java) ?: error("Failed to find map serializer")

        if (typeSer != null)
            ser.serializeWithType(map, gen, provider, typeSer)
        else
            ser.serialize(map, gen, provider)
    }
}
