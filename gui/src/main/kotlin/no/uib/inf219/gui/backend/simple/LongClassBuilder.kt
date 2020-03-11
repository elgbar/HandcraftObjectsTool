package no.uib.inf219.gui.backend.simple

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.util.converter.LongStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder

class LongClassBuilder(
    initial: Long = 0,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Long>(
        Long::class.java, initial, name, parent, prop, immutable,
        LongStringConverter()
    ) {}