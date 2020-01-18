package no.uib.inf219.gui.loader

import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import kotlin.collections.ArrayList


/**
 * @author Elg
 */
object DynamicClassLoader {

    private val fileClassLoaders: MutableMap<File, ClassLoader> = ConcurrentHashMap()
    private val fileClasses: MutableMap<File, List<Class<*>>> = ConcurrentHashMap()
    private val classes: MutableMap<String, Class<*>> = ConcurrentHashMap()
    private val allClasses: MutableList<Class<*>> = ArrayList()

    private var recalcSuperList: Boolean = true


    fun loadFile(file: File) {
        check(!fileClassLoaders.containsKey(file)) { "The file ${file.name} has already been loaded" }

        val cl: ClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
        fileClassLoaders[file] = cl
        val fileClasses = ArrayList<Class<*>>()

        val zipFile = ZipFile(file)
        val entries: Enumeration<out ZipEntry> = zipFile.entries()

        if (entries.hasMoreElements()) {
            while (entries.hasMoreElements()) {
                val elem = entries.nextElement()
                val name = elem.name
                if (elem.isDirectory || !name.endsWith(".class")) {
                    continue
                }
                val className = name.removeSuffix(".class").replace('/', '.')
//                println("className = ${className}")
                val clazz: Class<*>
                try {
                    clazz = cl.loadClass(className)
                } catch (e: NoClassDefFoundError) {
                    println("Failed to load class $className due to ${e.javaClass.name}")
                    continue
                } catch (e: IncompatibleClassChangeError) {
                    println("Failed to load class $className due to ${e.javaClass.name}")
                    continue
                } catch (e: UnsupportedClassVersionError) {
                    println("Failed to load class $className due to ${e.javaClass.name}")
                    continue
                }
                fileClasses += clazz
                classes[name] = clazz

            }
        } else {
            throw IllegalArgumentException("Given file is not a zip file")
        }
        recalcSuperList = true
        this.fileClasses[file] = fileClasses
    }

    fun classForName(className: String): Class<*>? {
        return classes[className]
    }

    fun classesFromFile(file: File): List<Class<*>>? {
        return fileClasses[file]
    }

    /**
     */
    fun getAllClasses(): List<Class<*>> {
        if (recalcSuperList) {
            recalcSuperList = false
            allClasses.clear()
            allClasses += fileClasses.flatMap { it.value }.toList()
        }
        return allClasses
    }
}
