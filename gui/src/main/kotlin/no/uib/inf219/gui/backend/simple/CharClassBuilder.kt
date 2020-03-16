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
        //TODO rework this
        return text.length <= 1
    }
}
