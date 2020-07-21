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

package no.uib.inf219.extra

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.EmptyClassBuilderNode

/**
 * @author Elg
 */

/**
 * Find a child item from [key] for the owning class builder (ie this.value.cb`)
 *
 * @throws IllegalStateException If the [TreeItem.value] is [EmptyClassBuilderNode], The class builder of the [TreeItem.value] is `null`, [ClassBuilderNode.item] does not match the item in this tree items children, but the key and value does
 * @throws IllegalArgumentException if no child with the given [key] and correct parent can be found
 */
fun TreeItem<ClassBuilderNode>.findChild(key: ClassBuilder): TreeItem<ClassBuilderNode> {
    if (value is EmptyClassBuilderNode) {
        error("Empty Class Builder node cannot have children! Trying to reset child ${key.getPreviewValue()} of ${value.key.getPreviewValue()}")
    }
    val parent = value?.cb
        ?: error("The owning class builder node does not have a class builder set or the value of this tree item is null")
    for (item in children) {
        val cbn = item.value ?: error("No CBN have been set for child $key of $parent")
        if (cbn.key.serObject == key.serObject && cbn.parent == parent) {
            check(cbn.item === item) {
                "Found child with matching key and parent but it's item is not the same object!\n" +
                    "Are they equal? ${cbn.item == item}\n" +
                    "child item: ${cbn.item} (hash ${Integer.toHexString(cbn.item.hashCode())})\n" +
                    "expected: $item (hash ${Integer.toHexString(cbn.item.hashCode())})"
            }
            return item
        }
    }
    throw IllegalArgumentException("Failed to find a child with the key $key where the parent is also equal")
}

val <T> TreeView<T>.selectedItem: TreeItem<T>?
    get() = this.selectionModel.selectedItem

fun <T> TreeView<T>.reselect() {
    with(selectionModel) {
        val oldSel = selectedItem
        clearSelection()
        select(oldSel)
    }
}

/**
 * Refreshes and call select event again
 */
fun TreeView<ClassBuilderNode>.reload() {
    this.refresh()
    reselect()
}
