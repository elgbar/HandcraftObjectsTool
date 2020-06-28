/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.extra

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
