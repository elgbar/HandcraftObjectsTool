package no.uib.inf219.gui.loader

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.io.SegmentedStringWriter
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.ser.PropertyWriter
import no.uib.inf219.extra.type
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.gui.view.OutputArea


/**
 * Retrieve information about java classes using Jackson
 *
 * @author Elg
 */
object ClassInformation {

    const val VALUE_DELEGATOR_NAME = "value"

    private var ser: DefaultSerializerProvider = createDSP()

    private val cache: MutableMap<JavaType, Triple<TypeSerializer, Map<String, PropertyMetadata>, Boolean>> = HashMap()
    private val typeCache: MutableMap<Class<*>, JavaType> = HashMap()


    private fun createDSP(): DefaultSerializerProvider {
        val jfac = JsonFactory.builder().build()
        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))
        val cfg: SerializationConfig = mapper.serializationConfig
        cfg.initialize(gen)

        mapper.typeFactory = mapper.typeFactory.withClassLoader(DynamicClassLoader)

        return DefaultSerializerProvider.Impl().createInstance(cfg, mapper.serializerFactory)
    }

    /**
     * Update the current serializable with the new mapper from [ControlPanelView.mapper]
     */
    fun updateMapper() {
        ser = createDSP()
    }

    data class PropertyMetadata(
        val name: String,
        val type: JavaType,
        val defaultValue: String,
        val required: Boolean,
        val description: String,
        val virtual: Boolean
    ) {

        fun getDefaultInstance(): Any? {
            return if (defaultValue.isEmpty()) {
                null
            } else {
                try {
                    mapper.readValue(defaultValue, type) as Any?
                } catch (e: Throwable) {
                    OutputArea.logln("Failed to parse default value for property '$name' of $type. Given string '$defaultValue'")
                    OutputArea.logln(e.localizedMessage)
                    null
                }
            }
        }
    }


    fun serializableProperties(type: JavaType): Triple<TypeSerializer?, Map<String, PropertyMetadata>, Boolean> {

        val realType = ser.findValueSerializer(type).handledType().type()

        return cache.computeIfAbsent(type) {

            val props = ser.findValueSerializer(realType)
            val map = HashMap<String, PropertyMetadata>()
            val valueDelegator: Boolean
            if (props.handledType() == type.rawClass) {
                valueDelegator = false
                props.properties().forEach { prop: PropertyWriter ->
                    map[prop.name] = PropertyMetadata(
                        prop.name,
                        prop.type,
                        prop.metadata?.defaultValue ?: "",
                        prop.isRequired,
                        prop.metadata?.description ?: "",
                        prop.isVirtual
                    )
                }
            } else {
                valueDelegator = true
                map[VALUE_DELEGATOR_NAME] =
                    PropertyMetadata(
                        VALUE_DELEGATOR_NAME,
                        props.handledType().type(),
                        "",
                        true,
                        "The parent class '${type.rawClass.canonicalName}' is being represented by this single value.",
                        true
                    )
            }
            return@computeIfAbsent Triple(ser.findTypeSerializer(it), map, valueDelegator)
        }
    }

    /**
     * Convert a java class into [JavaType]
     *
     * Results are cached
     */
    fun toJavaType(clazz: Class<*>): JavaType {
        return typeCache.computeIfAbsent(clazz) {
            mapper.typeFactory.constructType(it)
        }
    }
}
