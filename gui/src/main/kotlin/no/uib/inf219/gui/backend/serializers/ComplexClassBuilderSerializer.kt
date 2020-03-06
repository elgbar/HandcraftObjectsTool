package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ComplexClassBuilder

/**
 * @author Elg
 */
class ComplexClassBuilderSerializer : StdSerializer<ComplexClassBuilder<*>>(ComplexClassBuilder::class.type()) {


    override fun serialize(value: ComplexClassBuilder<*>, gen: JsonGenerator, provider: SerializerProvider) {
        //
        serializeWithType(value, gen, provider, value.typeSerializer)
    }

    override fun serializeWithType(
        value: ComplexClassBuilder<*>,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {

        gen.writeStartObject()

        if (typeSer?.typeInclusion == JsonTypeInfo.As.PROPERTY) {
            gen.writeStringField(typeSer.propertyName, value.type.rawClass.canonicalName)
        }

        for ((field, obj) in value.serObject) {
            gen.writeObjectField(field, obj)
        }
        gen.writeEndObject()
    }
}
