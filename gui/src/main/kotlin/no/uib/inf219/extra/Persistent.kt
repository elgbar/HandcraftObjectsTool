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
open class Persistent<T : Serializable>(val default: T? = null) {

    companion object {
        const val PERSISTENT_FOLDER = "persistent"
        val persistentFolderFile get() = hotApplicationHome().child(PERSISTENT_FOLDER).ensureFolder()
    }

    private var cache: T? = null
    private var haveBeenRead = false

    private fun getFile(thisRef: Any, property: KProperty<*>): File {
        return persistentFolderFile.child("${thisRef.javaClass.name}-${property.name}.ser")
    }

    open operator fun getValue(thisRef: Any, property: KProperty<*>): T? {
        synchronized(this) {
            if (haveBeenRead) return cache

            val file = getFile(thisRef, property)
            if (!file.exists()) return default

            haveBeenRead = true

            val value = try {
                @Suppress("UNCHECKED_CAST")
                file.objectInputStream().use { it.readObject() as T? }
            } catch (e: Throwable) {
                // well that didn't work lets burn everything
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

            file.objectOutputStream().use { it.writeObject(value) }
        }
    }

    fun resetValue(thisRef: Any, property: KProperty<*>) {
        cache = null
        haveBeenRead = false
        getFile(thisRef, property).delete()
    }
}
