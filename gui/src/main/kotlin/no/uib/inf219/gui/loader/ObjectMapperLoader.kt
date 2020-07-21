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

package no.uib.inf219.gui.loader

import com.fasterxml.jackson.databind.ObjectMapper
import javafx.application.Platform
import no.uib.inf219.gui.view.LoggerView.log
import tornadofx.FX
import tornadofx.error
import java.io.File
import java.lang.reflect.Field
import java.util.Enumeration
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * @author Elg
 */
object ObjectMapperLoader {
    const val PATH_TO_OBJECT_FILE = ".hot"
    const val DEFAULT_FIELD_NAME = "mapper"

    /**
     * Find a field within a class within the given class that returns an [ObjectMapper] when called statically.
     *
     * To make sure this method can find the object mapper a files with the name [PATH_TO_OBJECT_FILE] must exist within the root directory of the loaded jar file.
     *
     * This method will then read that file and expects there to be at least one line.
     *
     * ## Content of file
     *
     * The first line must always be the path to the class in which the object mapper field is located.
     *
     * The second line is optional but if specified it must contain the name of a static field that holds an ObjectMapper.
     * If not present the default field name [DEFAULT_FIELD_NAME] is used.
     *
     */
    fun findObjectMapper(file: File): ObjectMapper? {

        val zipFile = ZipFile(file)

        val entries: Enumeration<out ZipEntry?> = zipFile.entries()

        var clazzPath: String? = null
        var fieldPath: String = DEFAULT_FIELD_NAME

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()!!
            if (entry.name == PATH_TO_OBJECT_FILE) {
                if (entry.isDirectory) {
                    log("Found a directory where the object file should be")
                    return null
                }

                val lines = zipFile.getInputStream(entry).bufferedReader().readLines()
                if (lines.isEmpty()) {
                    Platform.runLater {
                        error(
                            "Failed to load Object Mapper from jar",
                            "No class specified to load Object Mapper from, file $file",
                            owner = FX.primaryStage
                        )
                    }
                    return null
                }
                clazzPath = lines[0]
                if (lines.size > 1) fieldPath = lines[1]
                break
            }
        }

        if (clazzPath == null) {
            // no file given
            return null
        }

        val clazz: Class<*>
        try {
            clazz = DynamicClassLoader.loadClass(clazzPath)
        } catch (e: Throwable) {
            Platform.runLater {
                error(
                    "Failed to load Object Mapper from jar",
                    "Failed to load class with object mapper '$clazzPath' in file $file\n" +
                        "${e.javaClass.simpleName}: ${e.message}",
                    owner = FX.primaryStage
                )
            }
            log(e)
            return null
        }

        val field: Field

        try {
            field = clazz.getDeclaredField(fieldPath)
            field.isAccessible = true
        } catch (e: Throwable) {
            Platform.runLater {
                error(
                    "Failed to load Object Mapper from jar",
                    "Failed to find the specified (or default) field '$fieldPath' within class '$clazzPath' in file $file\n" +
                        "${e.javaClass.simpleName}: ${e.message}",
                    owner = FX.primaryStage
                )
            }
            log(e)
            return null
        }

        val mapper: ObjectMapper
        try {
            mapper = field.get(null) as ObjectMapper
        } catch (e: ClassCastException) {
            val className = try {
                field.type.name
            } catch (e: Throwable) {
                "unknown type (${e.javaClass.simpleName} was thrown)"
            }
            Platform.runLater {
                error(
                    "Failed to load Object Mapper from jar",
                    "Given field for object mapper is not an object mapper nor any subclass of object mapper!\n" +
                        "Field '$fieldPath' (of type $className) in class '$clazzPath' in file $file\n" +
                        "${e.javaClass.simpleName}: ${e.message}",
                    owner = FX.primaryStage
                )
            }
            log(e)
            return null
        } catch (e: Throwable) {
            Platform.runLater {
                error(
                    "Failed to load Object Mapper from jar",
                    "Failed to get the instance of the field. Field '$fieldPath' in class '$clazzPath' in file $file\n" +
                        "${e.javaClass.simpleName}: ${e.message}",
                    owner = FX.primaryStage
                )
            }
            log(e)
            return null
        }

        return mapper
    }
}
