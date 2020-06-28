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

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.extra.child
import no.uib.inf219.extra.ensureFolder
import no.uib.inf219.extra.hotApplicationHome
import no.uib.inf219.extra.type
import no.uib.inf219.gui.GuiMain.Companion.FILES_FOLDER
import no.uib.inf219.gui.view.LoggerView
import org.jetbrains.annotations.Contract
import java.io.File
import java.net.URLClassLoader

/**
 * A class to help load files from multiple different sources.
 *
 * @author Elg
 */
object DynamicClassLoader : URLClassLoader(emptyArray()) {

    init {
        val filesFolder = hotApplicationHome().child(FILES_FOLDER).ensureFolder()
        val files = filesFolder.listFiles()
        if (files != null) {
            for (file in files) {
                loadFile(file)
            }
        }
    }

    /**
     * Load all classes from the given [File], if file is already loaded nothing will be done
     *
     * @param file The file to load
     */
    fun loadFile(file: File) {
        try {
            addURL(file.toURI().toURL())
        } catch (e: Throwable) {
            LoggerView.log("Failed to load jar file ${file.absolutePath}")
            LoggerView.log("$e")
            e.printStackTrace()
        }
        LoggerView.log("Successfully loaded jar file ${file.absolutePath}")
    }

    /**
     * Load a jackson java type from the binary name of the class.
     *
     * @see ClassLoader.loadClass
     * @see ClassInformation.toJavaType
     */
    @Contract("null->null;!null->!null")
    fun loadType(name: String?): JavaType? {
        return loadClass(name ?: return null).type()
    }

    /**
     * Parse a class name into a [JavaType].
     *
     * This method allows users to write class names in a more natural form. For example:
     * * Write primitive classes using their keyword.
     *    * For example `int` becomes [Int]
     * * Write N-dimensional array using the normal `[]` suffix for the number of arrays.
     *    * `int[]` becomes [IntArray]
     *    * `int[][]` becomes `Array<IntArray>`
     *    * `java.lang.String[]` becomes `Array<String>>`
     *    * `java.lang.Integer[]` becomes `Array<Int>>`
     *
     * @throws IllegalArgumentException If the array suffix is malformed
     * @throws ClassCastException If an illegal class name is used
     *
     * @see ClassLoader.loadClass
     * @see ClassInformation.toJavaType
     *
     * @return A [JavaType] representation of the given class name
     */
    fun parseClassName(className: String): JavaType {
        return when (className) {
            "int" -> Int::class.java
            "long" -> Long::class.java
            "byte" -> Byte::class.java
            "short" -> Short::class.java
            "float" -> Float::class.java
            "double" -> Double::class.java
            "boolean" -> Boolean::class.java
            "char" -> Char::class.java
            else -> {
                //okay it is not a primitive class, maybe an array?

                val countOpen = className.count { it == '[' }
                val countClosed = className.count { it == ']' }
                if (countOpen != countClosed) {
                    throw IllegalArgumentException("Uneven number of square parentheses, found $countOpen open and $countClosed closed square parentheses")
                }

                val arrayDims =
                    className.filterIndexed { i, c -> c == '[' && i + 1 < className.length && className[i + 1] == ']' }.length

                if (arrayDims != countOpen) {
                    throw IllegalArgumentException("Wrong order of parenthesis, expected the class to have $countOpen dimensions while only $arrayDims was found")
                }

                val fixedName: String =
                    if (arrayDims > 0) {
                        //It's an array, but is it primitive?
                        val binName = when {
                            className.startsWith("int") -> "I"
                            className.startsWith("long") -> "J"
                            className.startsWith("byte") -> "B"
                            className.startsWith("short") -> "S"
                            className.startsWith("float") -> "F"
                            className.startsWith("double") -> "D"
                            className.startsWith("boolean") -> "Z"
                            className.startsWith("char") -> "C"
                            //not primitive, but a class
                            else -> "L${className.substring(0, className.length - arrayDims * 2)};"
                        }
                        //Append the number of array dimensions back onto the binary name
                        "${"[".repeat(arrayDims)}$binName"
                    } else {
                        //not array, nor primitive must be normal class
                        className
                    }
                Class.forName(fixedName, true, DynamicClassLoader)
            }
        }.type()
    }
}
