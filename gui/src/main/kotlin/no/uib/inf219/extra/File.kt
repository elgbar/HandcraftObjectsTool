package no.uib.inf219.extra

import java.io.*

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
