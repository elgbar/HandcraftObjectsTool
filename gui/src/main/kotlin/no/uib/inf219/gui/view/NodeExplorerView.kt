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

        @Suppress("UNCHECKED_CAST")
        fun childFactory(it: TreeItem<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>>): Iterable<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>>? {
            val cb = it.value.middle
            return when {
                cb == null -> null
                cb.isLeaf() -> null
                cb is ReferenceClassBuilder -> null //break cycles
                else -> cb.getSubClassBuilders().map { (key, child) -> MutableTriple(key.getPreviewValue(), child, cb) }
            }
        }
        populate { childFactory(it) }

        cellFormat {
            text = it.left

            tooltip("Class: ${it.middle?.type ?: it.right.getChildType(it.left.toCb())}")

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
                val ref = selector.createReference(type, key, value.right)

                if (ref == null) {
                    warning(
                        "No reference returned",
                        "No reference was returned from the search. This could be because you canceled the search (pressed escape) or because the chosen class builder was invalid."
                    )
                    return@action
                }

                //register the new reference but first null out any old reference
                value.right.resetChild(key, restoreDefault = false)
                value.right.createClassBuilderFor(key, ref)

                //reload parent view (or this view if root controller)
                (controller.parentController ?: controller).reloadView()
            }

            fun resetClicked(restoreDefault: Boolean) {
                val item = this@treeview.selectionModel.selectedItem ?: return
                val value = item.value

                val key = value.left.toCb()

                //reset the clicked item
                value.right.resetChild(key, value.middle, restoreDefault)

                //Now get the new instance of it (if any)
                val newCb = value.right.getChild(key)

                //If we are resetting the root we need to make sure we select the correct parent
                val parent = if (value.right == controller.rootCb.parent) controller.rootCb else value.right

                //we must update the property editor area otherwise we will be editing a stale object
                controller.currSel = MutableTriple(value.left, newCb, parent)

                //remove all children from the tree view
                item.children.clear()
                if (newCb != null) {

                    //and replace them with the new children if the new class builder have a default value
                    val children = newCb.getSubClassBuilders().map { (k, cb) ->
                        TreeItem(MutableTriple(k.getPreviewValue(), cb, parent))
                    }
                    item.children.setAll(children)
                }
            }

            item("Restore to default") {
                action { resetClicked(true) }
            }


            item("Set to null") {
                action { resetClicked(false) }
            }
        }
    }
}
