package no.uib.inf219.extra

import com.fasterxml.jackson.databind.ser.PropertyWriter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.backend.simple.IntClassBuilder
import no.uib.inf219.gui.backend.simple.StringClassBuilder

/**
 * Create [ClassBuilder]s from primitives
 *
 * @author Elg
 */
fun String.toCb(
    key: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = true
): SimpleClassBuilder<String> {
    return StringClassBuilder(this, key, parent, prop, immutable)
}

fun Int.toCb(
    key: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: PropertyWriter? = null,
    immutable: Boolean = true
): SimpleClassBuilder<Int> {
    return IntClassBuilder(this, key, parent, prop, immutable)
}
