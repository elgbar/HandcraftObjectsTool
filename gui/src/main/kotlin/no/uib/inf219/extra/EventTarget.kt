package no.uib.inf219.extra

import javafx.beans.property.StringProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.control.Hyperlink
import javafx.scene.layout.VBox
import javafx.scene.text.TextAlignment
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import tornadofx.*
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


fun EventTarget.centeredText(vararg lines: String, op: VBox.() -> Unit = {}) {
    vbox {
        alignment = Pos.CENTER
        textflow {
            textAlignment = TextAlignment.CENTER
            for (line in lines.dropLast(1)) {
                text(line + "\n")
            }
            //do not add a newline to the last element
            text(lines.last())
        }
        op()
    }
}


fun EventTarget.internetHyperlink(text: String, url: String = text, op: Hyperlink.() -> Unit = {}) {
    hyperlink(text, op = op).action {
        openWebPage(url)
    }
}

fun openWebPage(url: String) {
    Desktop.getDesktop().browse(URI(url));
}
