package no.uib.inf219.extra

import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.backend.primitive.IntClassBuilder
import no.uib.inf219.gui.backend.primitive.StringClassBuilder

/**
 * Create [ClassBuilder]s from primitives
 *
 * @author Elg
 */
fun String.toCb(
    name: String = this,
    parent: ClassBuilder<*>? = null,
    immutable: Boolean = true
): SimpleClassBuilder<String> {
    return StringClassBuilder(this, name, parent, immutable = immutable)
}

fun Int.toCb(name: String, parent: ClassBuilder<*>? = null, immutable: Boolean = true): SimpleClassBuilder<Int> {
    return IntClassBuilder(this, name, parent, immutable = immutable)
}
