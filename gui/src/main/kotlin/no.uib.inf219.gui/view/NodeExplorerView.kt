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
class NodeExplorerView(private val controller: ObjectEditorController) : Fragment("Tree Explorer") {


    override val root = treeview<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>> {
        root = TreeItem(controller.currSel)
        root.isExpanded = true

        cellFormat {
            text = it.left

            setOnMouseClicked { event ->
                //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
                if (event.clickCount == 1 && event.button == MouseButton.PRIMARY) {
                    //double left click on an item

                    println("name = ${it.left} | middle = ${it.middle?.type} | right = ${it.right?.type}")
                    //first time we click it we want to create it
                    if (it.middle == null && !it.right.isLeaf()) {

                        val cb = it.right.createClassBuilderFor(it.left)
                        it.middle = cb
                        if (cb != null) {

                            //when creating a sub class builder for the first time we need to find all possible
                            // properties that can be edited
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
                val item = this@treeview.selectionModel.selectedItem ?: return@action
                val value = item.value
                if (item == root) return@action


                //when viewing the item that is being reset change the current viewed item to root
                // as otherwise the user is editing a stale object
//                if (isSuper(controller.currSel.middle, value.middle)) {
                controller.currSel = root.value
//                }
                //clear the backend values
                val rem = value.right.reset(value.left)
                if (rem)
                    value.middle = null

                //remove the visual items
                item.children.clear()

                this@treeview.parent
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
