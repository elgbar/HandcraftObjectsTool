package no.uib.inf219.extra

import tornadofx.FX
import java.io.File

/**
 * @author Elg
 */

val HOME_PATH: String = System.getProperty("user.home") + File.separator

/**
 * @return The home folder of the running user
 */
fun homeFolder(child: String = ""): File {
    return if (child.isEmpty()) File(HOME_PATH) else File(HOME_PATH, child)
}

/**
 * Return the folder of this application. The folder is guaranteed to exist
 * @throws NullPointerException If the primary stage does not have a title
 */
fun applicationHome(): File {
    return homeFolder("." + FX.primaryStage.title!!).apply {
        mkdirs()
    }
}
