package no.uib.inf219.gui.backend.simple

import javafx.util.converter.IntegerStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleNumberClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

class IntClassBuilder(
    initial: Int = 0,
    key: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) : SimpleNumberClassBuilder<Int>(Int::class.java, initial, key, parent, prop, immutable, IntegerStringConverter()) {}
