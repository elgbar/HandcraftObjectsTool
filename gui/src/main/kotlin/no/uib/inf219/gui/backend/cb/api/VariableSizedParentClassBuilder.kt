package no.uib.inf219.gui.backend.cb.api

import javafx.event.EventTarget
import javafx.scene.control.ContextMenu
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Region
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import no.uib.inf219.extra.centeredText
import no.uib.inf219.extra.reload
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * A [ParentClassBuilder] that does not have a fixed number of children.
 * This allows the user to add and remove children at will.
 *
 * @author Elg
 *
 * @see no.uib.inf219.gui.backend.cb.parents.CollectionClassBuilder
 * @see no.uib.inf219.gui.backend.cb.parents.MapClassBuilder
 */
abstract class VariableSizedParentClassBuilder : ParentClassBuilder() {

    /**
     * Create a new child
     */
    abstract fun createNewChild(): ClassBuilder?

    /**
     * Clear all elements from the collection/map of children
     *
     * Something like `serObject.clear()` is enough
     *
     * @see MutableCollection.clear
     * @see MutableMap.clear
     */
    protected abstract fun clear()

    /**
     * If it is variable sized it cannot be immutable
     */
    final override fun isImmutable() = false

    /**
     * Helper method to make sure the newly made object is expanded
     */
    private fun createNewChildAndExpand() {
        val created = createNewChild()
        item.isExpanded = true
        created?.item?.isExpanded = true
    }

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Region {
        return parent.borderpane {
            center {

                fun updatedText(): String {
                    return "There are ${this@VariableSizedParentClassBuilder.getChildren().size} elements in this collection"
                }

                centeredText(updatedText()) {
                    button("Add new element").action {
                        createNewChildAndExpand()

                        val tf = (this.children[0] as TextFlow).children[0] as Text
                        tf.text = updatedText()
                    }
                }
            }
        }
    }

    override fun onNodeKeyEvent(event: KeyEvent, controller: ObjectEditorController) {
        super.onNodeKeyEvent(event, controller)

        if (event.code == KeyCode.ENTER || event.code == KeyCode.SPACE) {
            createNewChildAndExpand()
            event.consume()
        }
    }

    override fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean {
        with(menu) {
            item("Add new entry").action { createNewChildAndExpand() }
            item("Clear").action {
                clear()
                item.children.clear()
                controller.tree.reload()
                item.isExpanded = false
            }
        }
        return true
    }

}
