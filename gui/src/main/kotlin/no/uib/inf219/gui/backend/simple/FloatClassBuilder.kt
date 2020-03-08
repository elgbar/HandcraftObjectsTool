package no.uib.inf219.gui.backend.simple

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.util.converter.FloatStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder

class FloatClassBuilder(
    initial: Float = 0.0f,
    name: String,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Float>(
        Float::class.java, initial, name, parent, prop, immutable,
        FloatStringConverter()
    ) {}
