package no.uib.inf219.gui.loader

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.ser.impl.MapEntrySerializer
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.gui.view.LoggerView
import tornadofx.findFieldByName


/**
 * Retrieve information about java classes using Jackson
 *
 * @author Elg
 */
object ClassInformation {

    const val VALUE_DELEGATOR_NAME = "value"
    const val MAP_ENTRY_KEY_NAME = "key"
    const val MAP_ENTRY_VALUE_NAME = "value"

    lateinit var ser: SerializerProvider
        private set
    private val typePropCache: MutableMap<JavaType, Triple<TypeSerializer, Map<String, PropertyMetadata>, Boolean>>
    private val typeCache: MutableMap<Class<*>, JavaType>

    init {
        typePropCache = HashMap()
        typeCache = HashMap()
    }

    private fun createDSP(): SerializerProvider {
//        val jfac = JsonFactory.builder().build()
//        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))
//        val cfg: SerializationConfig = mapper.serializationConfig
//        cfg.initialize(gen)

        mapper.typeFactory = mapper.typeFactory.withClassLoader(DynamicClassLoader)

//        return DefaultSerializerProvider.Impl().createInstance(cfg, mapper.serializerFactory)
        return mapper.serializerProviderInstance
    }

    /**
     * Update the current serializable with the new mapper from [ControlPanelView.mapper]
     */
    fun updateMapper() {
        ser = createDSP()
        typePropCache.clear()
        typeCache.clear()
    }

    data class PropertyMetadata(
        val name: String,
        val type: JavaType,
        val defaultValue: String,
        val required: Boolean,
        val description: String,
        val virtual: Boolean
    ) {
        private var validDefInst: Boolean? = null

        fun hasValidDefaultInstance(): Boolean {
            if (validDefInst == null) {
                validDefInst = getDefaultInstance() != null
            }
            return validDefInst!!
        }

        fun getDefaultInstance(): Any? {
            return if (defaultValue.isEmpty()) {
                null
            } else {
                try {
                    mapper.readValue(defaultValue, type) as Any?
                } catch (e: Throwable) {
                    LoggerView.log("Failed to parse default value for property '$name' of $type. Given string '$defaultValue'")
                    LoggerView.log(e.localizedMessage)
                    null
                }
            }
        }
    }


    fun serializableProperties(type: JavaType): Triple<TypeSerializer?, Map<String, PropertyMetadata>, Boolean> {
        return typePropCache.computeIfAbsent(type) {

            val mixIn = mapper.findMixInClassFor(type.rawClass)
            val realType = mixIn?.type() ?: type

            val props = ser.findValueSerializer(realType)
            val map = HashMap<String, PropertyMetadata>()
            val valueDelegator: Boolean

            when (props) {
                is JsonValueSerializer -> {
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
                is MapEntrySerializer -> {
                    valueDelegator = false

                    fun getType(name: String): JavaType {
                        val field = MapEntrySerializer::class.java.findFieldByName(name)
                            ?: error("Failed to find field '$name' in ${MapEntrySerializer::class.simpleName}")
                        field.isAccessible = true

                        val obj = field.get(props)
                        return obj as JavaType
                    }

                    val keyType = getType("_keyType")
                    val valueType = getType("_valueType")


                    map[MAP_ENTRY_VALUE_NAME] = PropertyMetadata(
                        MAP_ENTRY_VALUE_NAME,
                        valueType,
                        "",
                        false,
                        "Value found with it's corresponding key",
                        true
                    )
                    map[MAP_ENTRY_KEY_NAME] = PropertyMetadata(
                        MAP_ENTRY_KEY_NAME,
                        keyType,
                        "",
                        true,
                        "The unique key to a value in a map",
                        true
                    )
                }
                else -> {
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
                }
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
