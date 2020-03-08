package no.uib.inf219.gui.backend.simple

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.util.converter.CharacterStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder

class CharClassBuilder(
    initial: Char = '\u0000',
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
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
