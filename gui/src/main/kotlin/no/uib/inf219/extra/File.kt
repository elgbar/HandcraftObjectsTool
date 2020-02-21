package no.uib.inf219.extra

import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * @author Elg
 */
fun File.child(child: String): File {
    return File(this, child)
}

fun File.objectOutputStream(): ObjectOutputStream {
    return ObjectOutputStream(outputStream())
}

fun File.objectInputStream(): ObjectInputStream {
    return ObjectInputStream(inputStream())
}
