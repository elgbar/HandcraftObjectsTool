package no.uib.inf219.gui.view

import javafx.application.Platform
import javafx.scene.control.Button
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Styles
import tornadofx.*
import java.io.PrintWriter

import java.io.StringWriter

import java.io.Writer


/**
 * @author Elg
 */
object OutputArea : View() {

    override val root = scrollpane(fitToHeight = true, fitToWidth = true).textarea {
        addClass(Styles.parent)
        this.text
        isEditable = false
    }

    fun logln(e: Throwable) {
        if (Settings.printStackTraceOnError) {
            val writer: Writer = StringWriter()
            e.printStackTrace(PrintWriter(writer))
            logln(writer.toString())
            e.printStackTrace()
        }
    }

    fun clearButton(): Button {
        return button("Clear Log") {
            action {
                root.clear()
            }
        }
    }

    /**
     * Log a message and append a newline
     */
    fun logln(msg: String) {
        Platform.runLater {
            root.appendText(msg + "\n")
        }
    }

    /**
     * Log a message and append a newline lazily
     */
    fun logln(msg: () -> String) {
        logln(msg())
    }
}

