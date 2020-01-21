package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.io.SegmentedStringWriter
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.type.TypeFactory
import no.uib.inf219.api.serialization.SerializationManager


/**
 * @author Elg
 */
class ObjectEditorController(var clazz: Class<*>) {

    lateinit var javaType: JavaType
    lateinit var serializer: JsonSerializer<Any>
    var bean: BeanProperty? = null

    init {
        set(clazz)
    }

    fun set(x: Class<*>) {
        clazz = x
        val tfac: TypeFactory = TypeFactory.defaultInstance()//.withClassLoader(cl)
        javaType = tfac.constructType(clazz)
        val jfac = JsonFactory.builder().build()
        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))

        val cfg: SerializationConfig = SerializationManager.mapper.serializationConfig
        cfg.initialize(gen)

        val ser: DefaultSerializerProvider =
            DefaultSerializerProvider.Impl().createInstance(cfg, SerializationManager.mapper.serializerFactory)
        serializer = ser.findTypedValueSerializer(javaType, true, null)
    }
}
