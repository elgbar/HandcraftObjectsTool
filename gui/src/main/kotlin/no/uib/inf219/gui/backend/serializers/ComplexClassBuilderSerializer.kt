package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.type.WritableTypeId
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ComplexClassBuilder
import tornadofx.findFieldByName

/**
 * @author Elg
 */
object ComplexClassBuilderSerializer : StdSerializer<ComplexClassBuilder<*>>(ComplexClassBuilder::class.type()) {


    override fun serialize(value: ComplexClassBuilder<*>, gen: JsonGenerator, provider: SerializerProvider) {

        serializeWithType(value, gen, provider, value.typeSerializer)
    }

    override fun serializeWithType(
        value: ComplexClassBuilder<*>,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {


        if (value.isJsonValueDelegator) {

            require(value.serObject.size == 1) { "Json delegated values can only have one property!. Found ${value.serObject.size}! ${value.serObject}" }
            val prop = value.serObject.values.first()!!
            val ser: JsonSerializer<Any> = provider.findValueSerializer(prop.javaClass.type())
            ser.serialize(prop, gen, provider)
            return
        }

        val beanSer = provider.findValueSerializer(value.type)
        var objIdWriter: ObjectIdWriter? = beanSer.javaClass.findFieldByName("_objectIdWriter").also {
            it?.isAccessible = true
        }?.get(beanSer) as ObjectIdWriter?

        val objectId: WritableObjectId?

        //begin with id stuff first. If we are referencing something
        // we only want to write the id of the object we're referencing (not any begin object stuff)
        if (objIdWriter != null) {
            //if there is no serializer for the ObjectIdWriter create one
            if (objIdWriter.serializer == null) {
                objIdWriter = objIdWriter.withSerializer(provider.findValueSerializer(objIdWriter.idType))!!
            }

            //this is ripped straight out BeanSerializerBase#_serializeWithObjectId
            checkNotNull(objIdWriter.serializer)

            objectId = provider.findObjectId(value.serObject, objIdWriter.generator)
            // If possible, write as id already
            if (objectId.writeAsId(gen, provider, objIdWriter)) {
                return
            }

            // If not, need to inject the id:
            val id = objectId.generateId(value.serObject)

            if (objIdWriter.alwaysAsId) {
                objIdWriter.serializer.serialize(id, gen, provider)
                return
            }
        } else {
            //no object id information so no object id to write
            objectId = null
        }

        val typeId: WritableTypeId?
        if (typeSer != null) {
            //write out the type of the class we're creating, not the raw value or value.serObject
            typeId = typeSer.typeId(null, JsonToken.START_OBJECT)

            //Set the id of the type based not on the object in WritableTypeId but rather
            // the class we're pretending we're serializing: value.type
            if (typeId.id == null) {
                typeId.id = typeSer.typeIdResolver.idFromValueAndType(null, value.type.rawClass)
            }
            gen.writeTypePrefix(typeId)
        } else {
            //if we do not have any type id just begin the object
            gen.writeStartObject()
            typeId = null //No type info given
        }

        //write the object id after beginning the object
        // do it in here as we are sure no delegation is happening
        objectId?.writeAsField(gen, provider, objIdWriter)

        for ((key, prop) in value.serObject) {

            // Null handling is bit different, check that first
            if (prop == null) {
                gen.writeNullField(key)
                continue
            }
            // then find serializer to use
            val ser: JsonSerializer<Any> = provider.findValueSerializer(prop.javaClass.type())
            gen.writeFieldName(key)
            ser.serialize(prop, gen, provider)
        }

        //close the object we're editing
        if (typeId != null)
            gen.writeTypeSuffix(typeId)
        else
            gen.writeEndObject()
    }
}
