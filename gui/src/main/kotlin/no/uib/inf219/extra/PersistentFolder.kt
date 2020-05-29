package no.uib.inf219.extra

import java.io.File
import kotlin.reflect.KProperty

/**
 * Make sure a persistent file is always a folder
 */
class PersistentFolder : Persistent<File>() {

    override operator fun getValue(thisRef: Any, property: KProperty<*>): File? {
        val file = super.getValue(thisRef, property)
        if (file != null && !file.isDirectory) {
            //make sure we cannot return a file that is not a folder
            super.setValue(thisRef, property, null)
            return null
        }
        return file;
    }
}
