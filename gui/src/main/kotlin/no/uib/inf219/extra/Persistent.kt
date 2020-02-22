package no.uib.inf219.extra

import java.io.File
import java.io.Serializable
import java.nio.file.Files
import kotlin.reflect.KProperty

/**
 * Store serializable objects between restarts. It has a very slow write and slow read as it will read and parse the file first time it is read and write each time.
 *
 * If an error with reading occurs it will be ignored and the file containing the value on disk will be deleted and the cached value will be `null`.
 *
 * If an error with writing the file or it is a folder all files in that folder will be overwritten with the serialized object
 *
 * It is not recommended to use this delegator for values that are written to often.
 *
 * @author Elg
 */
class Persistent<T : Serializable> {

    private var cache: T? = null
    private var haveBeenRead = false

    private fun getFile(thisRef: Any, property: KProperty<*>): File {
        return applicationHome().child("${thisRef.javaClass.canonicalName}-${property.name}.ser")
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        synchronized(this) {
            if (haveBeenRead) return cache

            val file = getFile(thisRef, property)
            if (!file.exists()) return null

            haveBeenRead = true

            val value = try {
                @Suppress("UNCHECKED_CAST")
                getFile(thisRef, property).objectInputStream().readObject() as T?
            } catch (e: Throwable) {
                file.deleteRecursively()
                null
            }
            cache = value
            return value
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        synchronized(this) {
            cache = value
            haveBeenRead = true
            val file = getFile(thisRef, property)
            if (value == null) {
                Files.deleteIfExists(file.toPath())
                return
            }

            if (!file.isFile) {
                file.deleteRecursively()
            }

            if (!file.exists()) {
                file.createNewFile()
            }
            file.objectOutputStream().writeObject(value)
        }
    }
}
