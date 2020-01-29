package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import javafx.scene.input.MouseButton
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(val controller: ObjectEditorController) : View("Tree Explorer") {


    override val root = treeview<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>> {
        root = TreeItem(controller.currSel)
        root.isExpanded = true

        cellFormat {
            text = it.left

            setOnMouseClicked { event ->
                //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
                if (event.clickCount == 2 && event.button == MouseButton.PRIMARY) {
                    //double left click on an item

                    println("left = ${it.left} | middle = ${it.middle?.javaType} | right = ${it.right?.javaType}")
                    //first time we click it we want to create it
                    if (it.middle == null && !it.right.isLeaf()) {

                        val cb = it.right.createClassBuilderFor(it.left)
                        it.middle = cb
                        if (cb != null) {

                            this.treeItem.children.addAll(
                                cb.getSubClassBuilders().map { elem ->
                                    TreeItem(
                                        MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>(
                                            elem.key, elem.value, cb
                                        )
                                    )
                                }
                            )
                            this@treeview.refresh()
                        }
                    }
                    controller.currSel = it
                }
            }
        }

        contextmenu {
            item("Remove").action {
                val item = selectedValue ?: return@action
                if (selectedValue == root.value) return@action

                //clear the backend values
                item.right.reset(item.left)
                item.middle = null

                //remove the visual items
                this@treeview.selectionModel.selectedItem.children.clear()
            }
        }

        @Suppress("UNCHECKED_CAST")
        populate {
            val cb = it.value.middle
            when {
                cb == null -> null
                cb.isLeaf() -> null
                else -> cb.getSubClassBuilders().map { elem ->
                    MutableTriple(elem.key, elem.value, cb)
                }
            }
        }
    }
}
