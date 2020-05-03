package no.uib.inf219.gui.view

import javafx.application.Platform
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Styles
import tornadofx.View
import tornadofx.addClass
import tornadofx.scrollpane
import tornadofx.textarea
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

/**
 * @author Elg
 */
object LoggerView : View() {

    override val root = scrollpane(fitToHeight = true, fitToWidth = true).textarea {
        addClass(Styles.parent)
        this.text
        isEditable = false
    }

    fun log(e: Throwable) {
        if (Settings.printStackTraceOnError) {
            val writer: Writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            log(writer.toString())
            e.printStackTrace()
        }
    }

    fun clear() {
        Platform.runLater {
            root.clear()
        }
    }

    /**
     * Log a message and append the suffix
     */
    fun log(msg: String = "", suffix: String = "\n") {
        Platform.runLater {
            root.appendText("$msg$suffix")
        }
    }

    /**
     * Log a message and append a newline lazily
     */
    fun log(msg: () -> String) {
        log(msg())
    }
}

