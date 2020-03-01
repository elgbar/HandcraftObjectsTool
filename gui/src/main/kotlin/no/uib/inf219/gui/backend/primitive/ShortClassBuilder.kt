package no.uib.inf219.gui.backend.primitive

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.util.converter.ShortStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder

class ShortClassBuilder(
    initial: Short = 0,
    name: String,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Short>(
        Short::class.java, initial, name, parent, prop, immutable,
        ShortStringConverter()
    ) {

}
