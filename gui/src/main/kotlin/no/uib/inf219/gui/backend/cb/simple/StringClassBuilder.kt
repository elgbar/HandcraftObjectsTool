package no.uib.inf219.gui.backend.cb.simple

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
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


    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.textarea {
            bindStringProperty(textProperty(), converter, serObjectObservable)
        }
    }

    override fun validate(text: String): Boolean {
        return true
    }
}
