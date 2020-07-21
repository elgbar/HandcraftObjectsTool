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

import javafx.scene.control.ButtonType
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.system.exitProcess

/**
 * @author Elg
 */

/**
 * Create a file pointing to a child of this file.
 *
 * This is the same as doing `File(this, child)`
 */
fun File.child(child: String): File {
    return File(this, child)
}

/**
 * Create an object output stream for this file.
 *
 * @throws FileNotFoundException if the file does not exist,is a directory rather than a regular file, or for some other reason cannot be opened for reading.
 */
fun File.objectOutputStream(): ObjectOutputStream {
    return ObjectOutputStream(outputStream())
}

/**
 * Create an object input stream for this file.
 *
 * @throws FileNotFoundException if the file does not exist,is a directory rather than a regular file, or for some other reason cannot be opened for reading.
 */
fun File.objectInputStream(): ObjectInputStream {
    return ObjectInputStream(inputStream())
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    this.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
}

val DELETE = ButtonType("Delete")
val RENAME = ButtonType("Rename")
val EXIT = ButtonType("Exit application")

/**
 * Make sure this file is a folder. If it is not the user will be asked what will be done.
 * Either delete the file, rename or exit the application
 *
 * @return this, for chaining
 */
fun File.ensureFolder(): File {
    if (this.isFile) {
        tornadofx.error(
            "The file at '$this' is a file, the application requires this to be a folder.",
            "To fix this choose an action, default to delete.",
            owner = null,
            buttons = *arrayOf(DELETE, RENAME, EXIT),
            actionFn = {
                when (it) {
                    DELETE -> this@ensureFolder.deleteRecursively()
                    RENAME -> this@ensureFolder.renameTo(this@ensureFolder.resolveSibling("${this@ensureFolder.name}-file"))
                    EXIT -> exitProcess(1)
                }
            }
        )
    }

    if (!exists() && !mkdirs()) {
        tornadofx.error("Failed to create folder '$this'. Application will exit")
        exitProcess(1)
    }
    return this
}
