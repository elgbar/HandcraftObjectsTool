package no.uib.inf219.example.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.commons.lang.WordUtils

/**
 * A series of different [ObjectMapper] considered to be default in the HOT system
 *
 * @author Elg
 */
object SerializationManager {

    /**
     * Class to simplify getting the available object mappers
     */
    enum class StdObjectMapper {

        JSON {
            override fun getObjectMapper(): ObjectMapper {
                return jsonMapper
            }
        },
        YAML {
            override fun getObjectMapper(): ObjectMapper {
                return yamlMapper
            }
        },
        KOTLIN_JSON {
            override fun getObjectMapper(): ObjectMapper {
                return kotlinJson
            }
        },
        KOTLIN_YAML {
            override fun getObjectMapper(): ObjectMapper {
                return kotlinYamlMapper
            }
        };

        abstract fun getObjectMapper(): ObjectMapper

        override fun toString(): String {
            return WordUtils.capitalizeFully(name.replace("_", " "))
        }

        companion object {

            fun fromObjectMapper(om: ObjectMapper): StdObjectMapper {
                for (value in values()) {
                    if (value.getObjectMapper() === om) return value
                }
                throw IllegalArgumentException("Given object mapper is not a standard object mapper")
            }
        }
    }

    /**
     * An instance of object mapper working with YAML and kotlin
     */
    val yamlMapper = ObjectMapper(YAMLFactory())

    /**
     * An instance of object mapper working with YAML and kotlin
     */
    val kotlinYamlMapper by lazy {
        val om = ObjectMapper(YAMLFactory())
        om.registerModule(
            KotlinModule(
                nullToEmptyCollection = true,
                nullToEmptyMap = true,
                nullIsSameAsDefault = true
            )
        )
        return@lazy om
    }

    /**
     * An instance of object mapper with no configuration
     */
    val jsonMapper by lazy { ObjectMapper() }

    /**
     * An instance of object mapper with no configuration, but kotlin module registered
     */
    val kotlinJson by lazy {
        val mapper = ObjectMapper()

        mapper.registerModule(
            KotlinModule(
                nullToEmptyCollection = true,
                nullToEmptyMap = true,
                nullIsSameAsDefault = true
            )
        )
        return@lazy mapper
    }

    @JvmStatic
    inline fun <reified T> ObjectMapper.readValue(str: String): T {
        return this.readValue(str, T::class.java)
    }
}
