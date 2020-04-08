package no.uib.inf219.gui.backend.simple

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.control.TreeItem
import javafx.util.converter.CharacterStringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.OutputArea
import org.apache.commons.text.StringEscapeUtils
import tornadofx.textfield

class CharClassBuilder(
    initial: Char = '\u0000',
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = false,
    item: TreeItem<ClassBuilderNode>
) : SimpleClassBuilder<Char>(
    Char::class,
    initial,
    key,
    parent,
    property,
    immutable,
    CharacterStringConverter(),
    item
) {


    override fun validate(text: String): Boolean {
        return StringEscapeUtils.unescapeJava(text).length == 1
    }

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.textfield {
            textFormatter = TextFormatter<Char>() {

                val text: String = it.controlNewText ?: return@TextFormatter null

                if (it.isContentChange && text.isNotEmpty() && !validate(text)) {
                    OutputArea.logln { "Failed to parse '$text' to ${this@CharClassBuilder.serObject::class.simpleName}" }
                    return@TextFormatter null
                }
                return@TextFormatter it
            }
            bindStringProperty(textProperty(), converter, serObjectObservable)
        }
    }
}
