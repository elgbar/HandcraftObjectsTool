package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import javafx.scene.input.MouseButton
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ReferenceClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(private val controller: ObjectEditorController) : Fragment("Tree Explorer") {

    override val root = scrollpane(
        fitToWidth = true,
        fitToHeight = true
    ).treeview<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>> {
        root = TreeItem(controller.rootSel)
        root.isExpanded = true

        cellFormat {
            text = it.left

            setOnMouseClicked { event ->
                //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
                if (event.clickCount == 1 && event.button == MouseButton.PRIMARY) {
                    //double left click on an item

                    //first time we click it we want to create it
                    if (it.middle == null && !it.right.isLeaf()) {

                        val cb = it.right.createClassBuilderFor(it.left.toCb())
                        it.middle = cb
                        if (cb != null) {

                            //when creating a sub class builder for the first time we need to find all possible
                            // properties that can be edited
                            this.treeItem.children.addAll(
                                cb.getSubClassBuilders().map { elem ->
                                    TreeItem(
                                        MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>(
                                            elem.key.getPreviewValue(), elem.value, cb
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
            item("Make reference to...").action {
                val item = this@treeview.selectionModel.selectedItem ?: return@action
                if (item == root) return@action
                val value = item.value
                if (value.right.isLeaf()) return@action

                val key = value.left.toCb()
                val type = value.right.getChildType(key)
                if (type == null) {
                    OutputArea.logln("Failed to find a the type of the child ${value.left} for ${value.right}")
                    return@action
                }
                val selector: ReferenceSelectorView = find("controller" to controller)
                val other = selector.createReference(value.right, type)

                value.right.createClassBuilderFor(key, other)

                //reload parent view (or this view if root controller)
                (controller.parent ?: controller).reloadView()

            }
            item("Remove").action {
                val item = this@treeview.selectionModel.selectedItem ?: return@action
                if (item == root) return@action
                val value = item.value

                //when viewing the item that is being reset change the current viewed item to root
                // as otherwise the user is editing a stale object
                controller.currSel = null
                //clear the backend values
                if (value.right != null)
                    value.right.resetChild(value.left.toCb(), value.middle)
                else
                    value.middle?.reset()

                //remove the visual items
                item.children.clear()

                //reload parent view (or this view if root controller)
                (controller.parent ?: controller).reloadView()
            }
        }

        @Suppress("UNCHECKED_CAST")
        populate {
            val cb = it.value.middle
            when {
                cb == null -> null
                cb.isLeaf() -> null
                cb is ReferenceClassBuilder -> null //break cycles
                else -> cb.getSubClassBuilders().map { (key, child) ->
                    MutableTriple(key.getPreviewValue(), child, cb)
                }
            }
        }
    }
}
