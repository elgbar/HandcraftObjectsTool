package no.uib.inf219.gui.loader

import no.uib.inf219.extra.applicationHome
import no.uib.inf219.extra.child
import no.uib.inf219.gui.GuiMain
import java.io.File
import java.net.URLClassLoader

/**
 * A class to help load files from multiple different sources.
 *
 * @author Elg
 */
object DynamicClassLoader :
    URLClassLoader(
        arrayOf(applicationHome().child("${GuiMain.FILES_FOLDER}/").also { it.mkdirs() }.toURI().toURL())
    ) {

    private val loadedFiles: MutableSet<File> = HashSet()

    /**
     * Load all classes from the given [File], if file is already loaded nothing will be done
     *
     * @param file The file to load
     * @param reload If all classes should be loaded again
     */
    fun loadFile(file: File, reload: Boolean = false) {
        if (reload && loadedFiles.contains(file)) return
        addURL(file.toURI().toURL())
    }

}
