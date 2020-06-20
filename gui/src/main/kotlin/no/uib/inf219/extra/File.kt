package no.uib.inf219.extra

import javafx.scene.control.ButtonType
import java.io.*
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
