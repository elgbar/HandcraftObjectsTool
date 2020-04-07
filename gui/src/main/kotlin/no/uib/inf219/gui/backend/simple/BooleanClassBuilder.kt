package no.uib.inf219.gui.backend.simple

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.util.converter.BooleanStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.bind
import tornadofx.checkbox

class BooleanClassBuilder(
    initial: Boolean = false,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleClassBuilder<Boolean>(
    Boolean::class,
    initial,
    key,
    parent,
    property,
    immutable,
    BooleanStringConverter(),
    item
) {

    override fun toView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.checkbox {
            bind(serObjectObservable)
        }
    }
}
