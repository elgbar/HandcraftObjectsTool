package no.uib.inf219.gui.backend.simple

import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.converter.UUIDStringConverter
import no.uib.inf219.gui.loader.ClassInformation
import java.util.*

/**
 * @author Elg
 */
class UUIDClassBuilder(
    initial: UUID,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    prop: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false
) : SimpleClassBuilder<UUID>(UUID::class.java, initial, name, parent, prop, immutable, UUIDStringConverter) {

    override fun validate(text: String): Boolean {

        return try {
            UUID.fromString(text)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
