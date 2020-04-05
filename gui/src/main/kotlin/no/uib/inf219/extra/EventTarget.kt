package no.uib.inf219.extra

import javafx.beans.property.StringProperty
import javafx.event.EventTarget
import no.uib.inf219.gui.backend.ClassBuilder
import tornadofx.text

/**
 * Create a text object that is dependent on the given class builder.
 * Every time the given [cb] changes the text will be updated.
 */
fun EventTarget.textCb(cb: ClassBuilder, value: ClassBuilder.() -> String) {

    text(value(cb)) {
        this.textProperty().bindCbText(cb, value)
    }
}


fun StringProperty.bindCbText(cb: ClassBuilder, value: ClassBuilder.() -> String) {
    cb.serObjectObservable.onChange {
        this.set(value(cb))
    }
}
