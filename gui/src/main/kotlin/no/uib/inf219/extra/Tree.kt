package no.uib.inf219.extra

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode

/**
 * @author Elg
 */

/**
 * Find a child item from [key] for the owning class builder (ie this.value.cb`)
 */
fun TreeItem<ClassBuilderNode>.findChild(key: ClassBuilder): TreeItem<ClassBuilderNode> {
    val parent = value?.cb
        ?: error("The owning class builder node does not have a class builder set or the value of this tree item is null")
    for (item in children) {
        val cbn = item.value
        if (cbn.key == key && cbn.parent == parent) {
            require(cbn.item === item) {
                "Found child with matching key and parent but it's item is not the same object!\n" +
                        "Are they equal? ${cbn.item == item}\n" +
                        "child item: ${cbn.item} (hash ${Integer.toHexString(cbn.item.hashCode())})\n" +
                        "expected: $item (hash ${Integer.toHexString(cbn.item.hashCode())})"
            }
            return item
        }
    }
    error("Failed to find a child with the key $key where the parent is also equal")
}

val <T> TreeView<T>.selectedItem: TreeItem<T>?
    get() = this.selectionModel.selectedItem

fun <T> TreeView<T>.reselect() {
    with(selectionModel) {
        val oldSel = selectedIndex
        clearSelection()
        select(oldSel)
    }
}
