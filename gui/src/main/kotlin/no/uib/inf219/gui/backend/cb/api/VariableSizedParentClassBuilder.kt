/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.center
import tornadofx.item

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
        expand()
        created?.expand()
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
        if (event.isConsumed) return

        if (event.code == KeyCode.ENTER || event.code == KeyCode.SPACE) {
            createNewChildAndExpand()
            event.consume()
        }
    }

    override fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean {
        with(menu) {
            item("Clear").action {
                clear()
                item.children.clear()
                controller.tree.reload()
                item.isExpanded = false
            }
            item("Add new entry").action { createNewChildAndExpand() }
        }
        return true
    }
}
