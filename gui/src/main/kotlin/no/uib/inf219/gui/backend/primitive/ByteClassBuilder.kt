package no.uib.inf219.gui.backend.primitive

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.util.converter.ByteStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder

class ByteClassBuilder(
    initial: Byte = 0,
    name: String,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Byte>(
        Byte::class.java, initial, name, parent, prop, immutable,
        ByteStringConverter()
    ) {
}
