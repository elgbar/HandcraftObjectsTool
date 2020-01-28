package no.uib.inf219.gui.loader

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.io.SegmentedStringWriter
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.TypeFactory
import no.uib.inf219.api.serialization.SerializationManager


/**
 * @author Elg
 */
object ClassInformation {

    private val tfac = TypeFactory.defaultInstance()
    val ser: DefaultSerializerProvider
    private val cache: MutableMap<JavaType, Map<String, JavaType>> = HashMap()
    private val typeCache: MutableMap<Class<*>, JavaType> = HashMap()

    init {
        val jfac = JsonFactory.builder().build()
        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))

        val cfg: SerializationConfig = SerializationManager.mapper.serializationConfig
        cfg.initialize(gen)

        ser = DefaultSerializerProvider.Impl().createInstance(cfg, SerializationManager.mapper.serializerFactory)
    }

    fun serializablePropertyNames(clazz: Class<*>): Set<String> {
        return serializablePropertyNames(toJavaType(clazz))
    }

    fun serializablePropertyNames(clazz: JavaType): Set<String> {
        return HashSet<String>(serializableProperties(clazz).keys)
    }

    fun serializableProperties(clazz: Class<*>): Map<String, JavaType> {
        return serializableProperties(toJavaType(clazz))
    }

    fun serializableProperties(clazz: JavaType): Map<String, JavaType> {
        return cache.computeIfAbsent(clazz) {
            val props = ser.findTypedValueSerializer(it, true, null).properties()
            val map = HashMap<String, JavaType>()
            props.forEach { prop: PropertyWriter ->
                map[prop.name] = prop.type
            }
            map
        }
    }

    fun toJavaType(clazz: Class<*>): JavaType {
        return typeCache.computeIfAbsent(clazz) {
            tfac.constructType(it)
        }
    }
}
