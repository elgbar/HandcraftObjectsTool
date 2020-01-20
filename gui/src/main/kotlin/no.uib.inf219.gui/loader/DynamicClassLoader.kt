package no.uib.inf219.gui.loader

import no.uib.inf219.gui.loader.DynamicClassLoader.getAllClasses
import java.io.File
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.zip.ZipEntry
import java.util.zip.ZipFile


/**
 * A class to help load files from multiple different sources.
 *
 * No classes will be loaded unless specified by for example using the [getAllClasses] method
 *
 * @author Elg
 */
object DynamicClassLoader {

    private val FILES: MutableMap<File, JarClassLoader> = ConcurrentHashMap()

    /**
     * @param file The file this classloader will control
     * @param priority The priority of this file. Lower number is higher priority
     */
    private class JarClassLoader(val file: File, val priority: Int) : Comparable<JarClassLoader> {
        val classLoader: ClassLoader = URLClassLoader(arrayOf(file.toURI().toURL()))
        private val classCache: MutableMap<String, Lazy<Class<*>>> = ConcurrentHashMap()

        init {
            val zipFile = ZipFile(file)
            val entries: Enumeration<out ZipEntry> = zipFile.entries()

            if (entries.hasMoreElements()) {
                while (entries.hasMoreElements()) {
                    val elem = entries.nextElement()
                    val name = elem.name
                    if (elem.isDirectory || !name.endsWith(".class") || name.endsWith("module-info.class")) {
                        continue
                    }
                    val className = name.removeSuffix(".class").replace('/', '.')
                    val clazz: Lazy<Class<*>>

                    //only load the class when needed
                    clazz = lazy {
                        try {
                            return@lazy classLoader.loadClass(className)
                        } catch (e: Throwable) {
                            throw IllegalStateException("Failed to load class '$className'", e)
                        }
                    }

                    this.classCache[className] = clazz

                }
            } else {
                throw IllegalArgumentException("Given file is not a zip file")
            }
        }

        fun classesFromFile(): Collection<Class<*>> {
            return classCache.values.map { it.value }
        }

        fun classForName(className: String): Class<*>? {
            return classCache[className]?.value
        }

        override fun compareTo(other: JarClassLoader): Int {
            return priority.compareTo(other.priority)
        }
    }

    /**
     * Load all classes from the given [File], if file is already loaded nothing will be done
     *
     * @param file The file to load
     * @param reload If all classes should be loaded again
     * @param priority The priority of this file. Lower number is higher priority
     */
    fun loadFile(file: File, reload: Boolean = false, priority: Int = Integer.MAX_VALUE) {
        if (reload && FILES.containsKey(file)) return
        FILES[file] = JarClassLoader(file, priority)
    }

    /**
     * This method searched through the supplied jar files in prioritized order.
     * Internally a lazy class loader is used to allow for lower memory overhead
     *
     * @return The class at the given path, or `null` if no class was found
     * @throws IllegalStateException If an exception was thrown if [ClassLoader.loadClass] throws.
     */
    fun classFromName(className: String): Class<*>? {
        return classWithLoaderFromName(className)?.first
    }

    /**
     * This method searched through the supplied jar files in prioritized order.
     * Internally a lazy class loader is used to allow for lower memory overhead
     *
     * @return The class at the given path, or `null` if no class was found
     * @throws IllegalStateException If an exception was thrown if [ClassLoader.loadClass] throws.
     */
    fun classWithLoaderFromName(className: String): Pair<Class<*>, ClassLoader>? {
        for (jcl in FILES.values.sorted()) {
            val clazz = jcl.classForName(className)
            if (clazz != null) {
                return Pair(clazz, jcl.classLoader)
            }
        }
        return null
    }


    /**
     * This class will load all classes in the loaded files, the memory usage will increase substantially.
     *
     * @return All classes loaded from the given [file]
     *
     */
    fun classesFromFile(file: File): List<Class<*>> {
        val jf: JarClassLoader =
            FILES[file] ?: throw IllegalArgumentException("The file ${file.name} has not been loaded")
        return jf.classesFromFile().toList()
    }


    /**
     * This class will load all classes in the loaded files, the memory usage will increase substantially.
     *
     * No caching will be done, this is the same as calling [classesFromFile] for every file loaded
     *
     * @return All classes loaded from external jar files
     *
     */
    fun getAllClasses(): List<Class<*>> {
        return FILES.values.flatMap { it.classesFromFile() }.toList()
    }

    /**
     * List of all files loaded
     */
    fun getLoadedFiles(): Set<File> {
        return FILES.keys
    }

    fun getClassloader(file: File): ClassLoader {
        val jcl = FILES[file] ?: throw java.lang.IllegalArgumentException("Given file not loaded")
        return jcl.classLoader
    }
}
