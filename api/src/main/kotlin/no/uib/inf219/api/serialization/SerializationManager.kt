package no.uib.inf219.api.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule


/**
 * @author Elg
 */
object SerializationManager {

    var mapper = ObjectMapper(YAMLFactory())

    init {
        mapper.findAndRegisterModules()
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        mapper.registerModule(
            KotlinModule(
                nullisSameAsDefault = true,
                nullToEmptyCollection = true
            )
        )
    }

    fun generateSchema(clazz: Class<*>): JsonSchema {
        return JsonSchemaGenerator(mapper).generateSchema(clazz)
    }

    /**
     * Convert the given object to YAML
     *
     * @return The given object as represented by YAML
     */
    @JvmStatic
    fun dump(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }

    /**
     * Shorthand for `dump(loadFromMap(map))`
     *
     * @return An instance of [T] with the properties of the given map
     */
    @JvmStatic
    inline fun <reified T> dumpMap(map: Map<String, Any?>): String {
        return dump(loadFromMap(map))
    }

    /**
     * Load an object from string
     *
     * @return An instance of [T] with the properties of the given string
     */
    @JvmStatic
    inline fun <reified T> load(str: String): T {
        return mapper.readValue(str, T::class.java)
    }

    /**
     * Load an object from map
     *
     * @return An instance of [T] with the properties of the given map
     */
    @JvmStatic
    inline fun <reified T> loadFromMap(map: Map<String, Any?>): T {
        return mapper.convertValue(map, T::class.java)
    }

    fun loadFromMap(map: Map<String, Any?>, clazz: Class<*>): Any {
        return mapper.convertValue(map, clazz)
    }
}
