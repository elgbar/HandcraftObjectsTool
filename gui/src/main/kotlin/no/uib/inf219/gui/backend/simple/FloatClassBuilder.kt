package no.uib.inf219.gui.backend.simple

import javafx.util.converter.FloatStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

class FloatClassBuilder(
    initial: Float = 0.0f,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Float>(
        Float::class.java, initial, name, parent, prop, immutable,
        FloatStringConverter()
    ) {}
