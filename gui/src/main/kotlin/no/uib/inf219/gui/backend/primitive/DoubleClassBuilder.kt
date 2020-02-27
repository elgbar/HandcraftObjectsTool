package no.uib.inf219.gui.backend.primitive

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.util.converter.DoubleStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder

class DoubleClassBuilder(
    initial: Double = 0.0,
    name: String,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null
) :
    SimpleNumberClassBuilder<Double>(
        Double::class.java, initial, name, parent, prop,
        DoubleStringConverter()
    ) {}
