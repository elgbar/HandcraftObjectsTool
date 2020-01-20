package no.uib.inf219.api.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
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
     * Load an object from YAML
     *
     * @return An instance of [T] with the properties of the given YAML
     */
    @JvmStatic
    inline fun <reified T> load(str: String): T {
        return mapper.readValue(str, T::class.java)
    }
}
