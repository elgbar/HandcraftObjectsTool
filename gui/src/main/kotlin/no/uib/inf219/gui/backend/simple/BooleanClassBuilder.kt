package no.uib.inf219.gui.backend.simple

import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.util.converter.BooleanStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.bind
import tornadofx.checkbox

class BooleanClassBuilder(
    initial: Boolean = false,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleClassBuilder<Boolean>(
        Boolean::class.java, initial, name, parent, prop, immutable,
        BooleanStringConverter()
    ) {
    override fun editView(parent: Pane): Node {
        return parent.checkbox {
            bind(serObjectObservable)
        }
    }
}
