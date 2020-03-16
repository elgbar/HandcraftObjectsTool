package no.uib.inf219.gui.backend.simple

import javafx.util.converter.ByteStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

class ByteClassBuilder(
    initial: Byte = 0,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Byte>(
        Byte::class.java, initial, name, parent, prop, immutable,
        ByteStringConverter()
    ) {
}
