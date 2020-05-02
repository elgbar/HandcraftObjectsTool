package no.uib.inf219.gui.loader

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.extra.applicationHome
import no.uib.inf219.extra.child
import no.uib.inf219.extra.type
import no.uib.inf219.gui.GuiMain
import java.io.File
import java.net.URLClassLoader

/**
 * A class to help load files from multiple different sources.
 *
 * @author Elg
 */
object DynamicClassLoader :
    URLClassLoader(
        arrayOf(applicationHome().child("${GuiMain.FILES_FOLDER}/").also { it.mkdirs() }.toURI().toURL())
    ) {

    private val loadedFiles: MutableSet<File> = HashSet()

    /**
     * Load all classes from the given [File], if file is already loaded nothing will be done
     *
     * @param file The file to load
     * @param reload If all classes should be loaded again
     */
    fun loadFile(file: File, reload: Boolean = false) {
        if (reload && loadedFiles.contains(file)) return
        addURL(file.toURI().toURL())
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

            "int[]" -> IntArray::class.java
            "long[]" -> LongArray::class.java
            "byte[]" -> ByteArray::class.java
            "short[]" -> ShortArray::class.java
            "float[]" -> FloatArray::class.java
            "double[]" -> DoubleArray::class.java
            "boolean[]" -> BooleanArray::class.java
            "char[]" -> CharArray::class.java
            else -> {
                val fixedName =
                    if (className.endsWith("[]")) "[L${className.removeSuffix("[]")};"
                    else className
                Class.forName(fixedName, true, DynamicClassLoader)
            }
        }.type()

    }

}
