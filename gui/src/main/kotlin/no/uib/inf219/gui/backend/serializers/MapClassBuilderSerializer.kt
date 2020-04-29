package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import no.uib.inf219.extra.toObject
import no.uib.inf219.gui.backend.MapClassBuilder
import no.uib.inf219.gui.backend.MapClassBuilder.Companion.ENTRY_KEY
import no.uib.inf219.gui.backend.MapClassBuilder.Companion.ENTRY_VALUE

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

            //explicitly convert the key object to it's type. This means that key elements cannot be referenced
            // or be a reference. But having a reference to a key in a map I am considering to be an edge case.
            //
            //This is because jackson is using a different type of serializers with keys as not everything is allowed
            // to be a key in json. So to make this happen we need to make key serializers for all existing cb
            // serializers and use in those to allow for referencing.
            val key = it.serObject[ENTRY_KEY]?.toObject()

            //value is allowed to be referenced and be a reference!
            val value = it.serObject[ENTRY_VALUE]
            key to value
        }.toMap()

        val ser =
            provider.findValueSerializer(map::class.java, null) ?: error("Failed to find map serializer")

        if (typeSer != null)
            ser.serializeWithType(map, gen, provider, typeSer)
        else
            ser.serialize(map, gen, provider)
    }
}
