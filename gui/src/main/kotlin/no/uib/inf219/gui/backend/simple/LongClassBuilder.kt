package no.uib.inf219.gui.backend.simple

import javafx.util.converter.LongStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

class LongClassBuilder(
    initial: Long = 0,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Long>(
        Long::class.java, initial, name, parent, prop, immutable,
        LongStringConverter()
    ) {}
