package no.uib.inf219.gui.backend.simple

import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Pane
import javafx.util.converter.CharacterStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.OutputArea
import org.apache.commons.text.StringEscapeUtils
import tornadofx.textfield

class CharClassBuilder(
    initial: Char = '\u0000',
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleClassBuilder<Char>(
        Char::class.java, initial, name, parent, prop, immutable,
        CharacterStringConverter()
    ) {

    override fun validate(text: String): Boolean {
        return StringEscapeUtils.unescapeJava(text).length == 1
    }

    override fun editView(parent: Pane): Node {
        return parent.textfield {
            textFormatter = TextFormatter<Char>() {

                val text: String = it.controlNewText ?: return@TextFormatter null

                if (it.isContentChange && text.isNotEmpty() && !validate(text)) {
                    OutputArea.logln { "Failed to parse '$text' to ${this@CharClassBuilder.serObject::class.simpleName}" }
                    return@TextFormatter null
                }
                return@TextFormatter it
            }
            bindStringProperty(textProperty(), converter, serObjectObservable)
        }
    }
}
