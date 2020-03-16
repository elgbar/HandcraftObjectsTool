package no.uib.inf219.gui.backend.simple

import javafx.util.converter.DoubleStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

class DoubleClassBuilder(
    initial: Double = 0.0,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) :
    SimpleNumberClassBuilder<Double>(
        Double::class.java, initial, name, parent, prop, immutable,
        DoubleStringConverter()
    ) {}
