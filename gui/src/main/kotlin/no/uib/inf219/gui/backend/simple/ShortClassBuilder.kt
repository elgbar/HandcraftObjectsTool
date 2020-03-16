package no.uib.inf219.gui.backend.simple

import javafx.util.converter.ShortStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

class ShortClassBuilder(
    initial: Short = 0,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Short>(
        Short::class.java, initial, name, parent, prop, immutable,
        ShortStringConverter()
    ) {

}
