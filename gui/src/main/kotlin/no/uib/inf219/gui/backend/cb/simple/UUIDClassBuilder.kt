package no.uib.inf219.gui.backend.cb.simple

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.converter.UUIDStringConverter
import no.uib.inf219.gui.loader.ClassInformation
import java.util.*

/**
 * @author Elg
 */
class UUIDClassBuilder(
    initial: UUID,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleClassBuilder<UUID>(UUID::class, initial, key, parent, property, immutable, UUIDStringConverter, item) {

    override fun validate(text: String): Boolean {

        return try {
            UUID.fromString(text)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
