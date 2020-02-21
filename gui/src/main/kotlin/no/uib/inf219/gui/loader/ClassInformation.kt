package no.uib.inf219.gui.loader

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.io.SegmentedStringWriter
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.TypeFactory
import no.uib.inf219.gui.view.ControlPanelView


/**
 * Retrieve information about java classes using Jackson
 *
 * @author Elg
 */
object ClassInformation {

    private val tfac = TypeFactory.defaultInstance()

    private var ser: DefaultSerializerProvider = createDSP()

    private val cache: MutableMap<JavaType, Pair<TypeSerializer, Map<String, PropertyWriter>>> = HashMap()
    private val typeCache: MutableMap<Class<*>, JavaType> = HashMap()


    private fun createDSP(): DefaultSerializerProvider {
        val jfac = JsonFactory.builder().build()
        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))
        val cfg: SerializationConfig = ControlPanelView.mapper.serializationConfig
        cfg.initialize(gen)

        return DefaultSerializerProvider.Impl().createInstance(cfg, ControlPanelView.mapper.serializerFactory)
    }

    /**
     * Update the current serializable with the new mapper from [ControlPanelView.mapper]
     */
    fun updateMapper() {
        ser = createDSP()
    }

    fun serializableProperties(clazz: Class<*>): Pair<TypeSerializer?, Map<String, PropertyWriter>> {
        return serializableProperties(toJavaType(clazz))
    }

    fun serializableProperties(type: JavaType): Pair<TypeSerializer?, Map<String, PropertyWriter>> {
        return cache.computeIfAbsent(type) {

            val props = ser.findValueSerializer(it).properties()
            val map = HashMap<String, PropertyWriter>()
            props.forEach { prop: PropertyWriter ->
                map[prop.name] = prop
            }
            ser.findTypeSerializer(it) to map
        }
    }

    /**
     * Convert a java class into [JavaType]
     *
     * Results are cached
     */
    fun toJavaType(clazz: Class<*>): JavaType {
        return typeCache.computeIfAbsent(clazz) {
            tfac.constructType(it)
        }
    }
}
