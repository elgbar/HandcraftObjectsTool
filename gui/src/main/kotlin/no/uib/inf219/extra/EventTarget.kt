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

import javafx.beans.property.StringProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Hyperlink
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import tornadofx.action
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.text
import tornadofx.textflow
import tornadofx.vbox
import java.awt.Desktop
import java.net.URI

/**
 * Create a text object that is dependent on the given class builder.
 * Every time the given [cb] changes the text will be updated.
 */
fun <T : ClassBuilder> EventTarget.textCb(cb: T, value: T.() -> String) {

    text(value(cb)) {
        this.textProperty().bindCbText(cb, value)
    }
}

fun <T : ClassBuilder> StringProperty.bindCbText(cb: T, value: T.() -> String) {
    cb.serObjectObservable.onChange {
        this@bindCbText.set(value(cb))
    }
}

fun EventTarget.centeredText(
    vararg lines: String,
    textAlignment: TextAlignment = TextAlignment.LEFT,
    op: VBox.() -> Unit = {}
) {
    hbox {
        alignment = Pos.CENTER
        vbox {
            alignment = Pos.CENTER
            textflow {
                this.textAlignment = textAlignment
                for (line in lines.dropLast(1)) {
                    text(line + "\n")
                }
                // do not add a newline to the last element
                text(lines.last())
            }
            op()
        }
    }
}

fun EventTarget.internetHyperlink(text: String, url: String = text, op: Hyperlink.() -> Unit = {}) {
    hyperlink(text, op = op).action {
        openWebPage(url)
    }
}

fun openWebPage(url: String) {
    Desktop.getDesktop().browse(URI(url))
}
