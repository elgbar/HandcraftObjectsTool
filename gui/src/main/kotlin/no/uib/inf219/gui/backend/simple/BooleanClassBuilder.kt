package no.uib.inf219.gui.backend.simple

import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.layout.Pane
import javafx.util.converter.BooleanStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode
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

    override fun editView(parent: Pane): Node {
        return parent.checkbox {
            bind(serObjectObservable)
        }
    }
}
