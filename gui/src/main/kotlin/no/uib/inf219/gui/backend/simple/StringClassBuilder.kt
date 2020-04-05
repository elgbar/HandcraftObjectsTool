package no.uib.inf219.gui.backend.simple

import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.layout.Pane
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.converter.StringStringConverter
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.textarea

/**
 * Note that the default value is the empty String `""` and not the default value `null`
 */

class StringClassBuilder(
    initial: String = "",
    key: ClassBuilder?,
    parent: ParentClassBuilder?,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleClassBuilder<String>(
    String::class,
    initial,
    key,
    parent,
    property,
    immutable,
    StringStringConverter,
    item
) {


    override fun editView(parent: Pane): Node {
        return parent.textarea {
            bindStringProperty(textProperty(), converter, serObjectObservable)
        }
    }

    override fun validate(text: String): Boolean {
        return true
    }
}
