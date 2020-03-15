package no.uib.inf219.gui.loader

import com.fasterxml.jackson.databind.ObjectMapper
import no.uib.inf219.gui.view.OutputArea.logln
import java.io.File
import java.lang.reflect.Field
import java.util.*
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
                    logln("Found a directory where the object file should be")
                    return null
                }

                val lines = zipFile.getInputStream(entry).bufferedReader().readLines()
                if (lines.isEmpty()) {
                    logln("No class specified in object file")
                    return null
                }
                clazzPath = lines[0]
                if (lines.size > 1) fieldPath = lines[1]
                break
            }
        }

        if (clazzPath == null) {
            //no file given
            return null
        }

        val clazz: Class<*>
        try {
            clazz = DynamicClassLoader.loadClass(clazzPath)
        } catch (e: ClassCastException) {
            logln("Failed to load class with object mapper '$clazzPath' in file $file")
            return null
        }

        val field: Field

        try {
            field = clazz.getDeclaredField(fieldPath)
        } catch (e: NoSuchFieldError) {
            logln("Failed to find the specified (or default) field '$fieldPath' within class '$clazzPath' in file $file")
            return null
        }

        val mapper: ObjectMapper
        try {
            mapper = field.get(null) as ObjectMapper
        } catch (e: Throwable) {
            logln("Given field for object mapper is not an object mapper nor any subclass of object mapper! Field '$fieldPath' in class '$clazzPath' in file $file")
            return null
        }

        return mapper
    }
}
