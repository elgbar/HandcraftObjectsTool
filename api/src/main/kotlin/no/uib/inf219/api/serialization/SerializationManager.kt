package no.uib.inf219.api.serialization

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule


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

        STD {
            override fun getObjectMapper(): ObjectMapper {
                return stdMapper
            }
        },
        YAML {
            override fun getObjectMapper(): ObjectMapper {
                return yamlMapper
            }
        },
        KOTLIN_STD {
            override fun getObjectMapper(): ObjectMapper {
                return kotlinStd
            }
        },
        KOTLIN_YAML {
            override fun getObjectMapper(): ObjectMapper {
                return kotlinYamlMapper
            }
        };

        abstract fun getObjectMapper(): ObjectMapper

        override fun toString(): String {
            return name.replace("_", " ").toLowerCase()
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
                nullisSameAsDefault = true,
                nullToEmptyCollection = true
            )
        )
        return@lazy om
    }

    /**
     * An instance of object mapper with no configuration
     */
    val stdMapper by lazy { ObjectMapper() }

    /**
     * An instance of object mapper with no configuration, but kotlin module registered
     */
    val kotlinStd by lazy {
        val mapper = ObjectMapper()

        mapper.registerModule(
            KotlinModule(
                nullisSameAsDefault = true,
                nullToEmptyCollection = true
            )
        )
        return@lazy mapper
    }

    @JvmStatic
    inline fun <reified T> ObjectMapper.readValue(str: String): T {
        return this.readValue(str, T::class.java)
    }

}
