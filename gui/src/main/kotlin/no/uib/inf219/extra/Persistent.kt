package no.uib.inf219.gui.extra

import java.io.File
import java.io.Serializable
import java.nio.file.Files
import kotlin.reflect.KProperty

/**
 * @author Elg
 */
class Persistent<T : Serializable> {

    private fun getFile(thisRef: Any, property: KProperty<*>): File {
        return applicationHome().child("${thisRef.javaClass.canonicalName}-${property.name}.ser")
    }

    operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        val file = getFile(thisRef, property)
        if (!file.exists()) return null

        return try {
            @Suppress("UNCHECKED_CAST")
            getFile(thisRef, property).objectInputStream().readObject() as T?
        } catch (e: Throwable) {
            null
        }
    }

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        val file = getFile(thisRef, property)
        if (value == null) {
            Files.deleteIfExists(file.toPath())
            return
        }

        if (!file.exists()) {
            file.createNewFile()
        }
        file.objectOutputStream().writeObject(value)
    }
}
