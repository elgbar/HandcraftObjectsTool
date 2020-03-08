package no.uib.inf219.gui.backend.simple

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.scene.Node
import javafx.scene.layout.Pane
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.converter.StringStringConverter
import tornadofx.textarea

/**
 * Note that the default value is the empty String `""` and not the default value `null`
 */
class StringClassBuilder(
    initial: String = "",
    name: String,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = false
) :
    SimpleClassBuilder<String>(
        String::class.java, initial, name, parent, prop, immutable,
        StringStringConverter
    ) {

    override fun editView(parent: Pane): Node {
        return parent.textarea {
            bindStringProperty(textProperty(), converter, serObjectProperty)
        }
    }

    override fun validate(text: String): Boolean {
        return true
    }
}
