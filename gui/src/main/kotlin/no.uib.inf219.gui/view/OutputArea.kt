package no.uib.inf219.gui.view

import javafx.application.Platform
import javafx.scene.control.Button
import no.uib.inf219.gui.Styles
import tornadofx.*

/**
 * @author Elg
 */
object OutputArea : View() {

    override val root = scrollpane(fitToHeight = true, fitToWidth = true).textarea {
        addClass(Styles.parent)
        isEditable = false
    }


    fun clearButton(): Button {
        return button("Clear") {
            action {
                root.clear()
            }
        }
    }


    /**
     * Log a message
     */
    fun log(msg: String) {
        Platform.runLater {
            root.appendText(msg)
        }
    }

    /**
     * Log a message and append a newline
     */
    fun logln(msg: String) {
        log(msg + "\n")
    }

    /**
     * Log a message lazily
     */
    fun log(msg: () -> String) {
        log(msg())
    }

    /**
     * Log a message and append a newline lazily
     */
    fun logln(msg: () -> String) {
        logln(msg())
    }
}

