package no.uib.inf219.gui.loader

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.extra.child
import no.uib.inf219.extra.ensureFolder
import no.uib.inf219.extra.hotApplicationHome
import no.uib.inf219.extra.type
import no.uib.inf219.gui.GuiMain.Companion.FILES_FOLDER
import no.uib.inf219.gui.view.LoggerView
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
    fun loadType(name: String): JavaType {
        return loadClass(name).type()
    }

    fun getType(className: String): JavaType {
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
                val arrayDims =
                    className.filterIndexed { i, c -> c == '[' && i + 1 < className.length && className[i + 1] == ']' }.length
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
                            //User is directly trying to craft a fully qualified name
                            className.startsWith('[') -> className
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
