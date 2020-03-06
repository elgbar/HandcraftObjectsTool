package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.BeanSerializer
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ComplexClassBuilder
import tornadofx.findFieldByName

/**
 * @author Elg
 */
class ComplexClassBuilderSerializer : StdSerializer<ComplexClassBuilder<*>>(ComplexClassBuilder::class.type()) {


    override fun serialize(value: ComplexClassBuilder<*>, gen: JsonGenerator, provider: SerializerProvider) {
        serializeWithType(value, gen, provider, value.typeSerializer)
    }

    override fun serializeWithType(
        value: ComplexClassBuilder<*>,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {

        val beanSer = provider.findValueSerializer(value.type) as BeanSerializer
        val objIdWriter: ObjectIdWriter? = beanSer.javaClass.findFieldByName("_objectIdWriter").also {
            it?.isAccessible = true
        }?.get(beanSer) as ObjectIdWriter?

        if (objIdWriter != null) {

            val ser = objIdWriter.serializer ?: provider.findValueSerializer(objIdWriter.idType)

            val serObjIdWriter = objIdWriter.withSerializer(ser)

            checkNotNull(serObjIdWriter.serializer)

            val objectId = provider.findObjectId(value.serObject, serObjIdWriter.generator)
            // If possible, write as id already
            if (objectId.writeAsId(gen, provider, serObjIdWriter)) {
                return
            }
            // If not, need to inject the id:
            val id = objectId.generateId(value.serObject)

            if (serObjIdWriter.alwaysAsId) {
                serObjIdWriter.serializer.serialize(id, gen, provider)
                return
            }

            gen.writeStartObject()
            objectId.writeAsField(gen, provider, serObjIdWriter)

        } else {
            gen.writeStartObject()
        }

        //include type information as a new property
        if (typeSer?.typeInclusion == JsonTypeInfo.As.PROPERTY) {
            gen.writeStringField(typeSer.propertyName, value.type.rawClass.canonicalName)
        }

        for ((key, prop) in value.serObject) {

            // Null handling is bit different, check that first
            if (prop == null) {
                gen.writeNullField(key)
                break
            }
            // then find serializer to use
            val ser: JsonSerializer<Any> = provider.findValueSerializer(prop::class.java)

            gen.writeFieldName(key)
            ser.serialize(prop, gen, provider)
        }

        gen.writeEndObject()
    }
}
