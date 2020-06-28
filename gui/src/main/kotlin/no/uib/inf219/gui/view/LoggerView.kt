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

