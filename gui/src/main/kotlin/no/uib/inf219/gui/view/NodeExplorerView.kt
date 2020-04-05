package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.MouseButton
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(private val controller: ObjectEditorController) : Fragment("Tree Explorer") {

    override val root = scrollpane(
        fitToWidth = true,
        fitToHeight = true
    ).treeview<ClassBuilderNode> {
        root = controller.rootCb.item
        root.isExpanded = true
        controller.tree = this

        repopulate()

        onUserSelect {
            controller.currSel = it
        }

        setOnMouseClicked { event ->
            //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
            if (event.clickCount == 1 && event.button == MouseButton.PRIMARY) {

                val oldItem = selectedValue?.ensurePresentClassBuilder()
//                repopulate()

//                fun recFind(parent: TreeItem<ClassBuilderNode>): TreeItem<ClassBuilderNode>{
//                    for (child in parent.children) {
//                        if(parent == child)
//                    }
//                }

//                root.children.forEach { it == selectedValue }
//                selectionModel.selectFirst()
                refresh()
            }
        }

        cellFormat {
            text = it.key.getPreviewValue()
            tooltip("Class: ${it.cb?.type ?: it.parent.getChildType(it.key)}")
        }

        contextmenu {

            item("Make reference to...").action {
                val item = this@treeview.selectionModel.selectedItem ?: return@action
                if (item == root) return@action
                val value = item.value
                if (value.parent.isLeaf()) return@action

                val key = value.key
                val type = value.parent.getChildType(key)
                if (type == null) {
                    OutputArea.logln("Failed to find a the type of the child ${value.key} for ${value.parent}")
                    return@action
                }
                val selector: ReferenceSelectorView = find("controller" to controller)
                val ref = selector.createReference(type, key, value.parent)

                if (ref == null) {
                    warning(
                        "No reference returned",
                        "No reference was returned from the search. This could be because you canceled the search (pressed escape) or because the chosen class builder was invalid."
                    )
                    return@action
                }

                //register the new reference but first null out any old reference
                value.parent.resetChild(key, restoreDefault = false)
                value.parent.createClassBuilderFor(key, ref)

                //reload parent view (or this view if root controller)
                (controller.parentController ?: controller).reloadView()
            }

            item("Restore to default") {

                action {
                    this@treeview.selectionModel.selectedItem?.resetClicked(this@treeview, true)
                }
            }

            item("Set to null") {
                action {
                    this@treeview.selectionModel.selectedItem?.resetClicked(this@treeview, false)
                }
            }
        }
    }

    companion object {

        fun TreeView<ClassBuilderNode>.repopulate() {
            root.children.clear()
            populate({ it.item }) {
                val cb = it.value.cb
                when {
                    cb == null || cb.isLeaf() -> null
                    (cb is ParentClassBuilder) -> cb.getTreeItems()
                    else -> null
                }
            }
        }

        fun TreeItem<ClassBuilderNode>.resetClicked(
            treeView: TreeView<ClassBuilderNode>,
            restoreDefault: Boolean
        ) {


            //reset the clicked item
            value = value.resetClassBuilder(restoreDefault)


//        treeView.root.children.clear()
//        treeView.populate { childFactory(treeView.root) }
//
            treeView.repopulate()
//        treeView.selectFirst()

//        reloadCell(treeView)
//            .reloadCell()
            //If we are resetting the root we need to make sure we select the correct parent
//                val parent = if (value.parent == controller.rootCb.parent) controller.rootCb else value.parent
//
//                //we must update the property editor area otherwise we will be editing a stale object
//                controller.currSel = null//MutableTriple(value.name, newCb, parent)
//
//                //remove all children from the tree view
//                item.children.clear()
//                if (newCb != null) {
//
//                    //and replace them with the new children if the new class builder have a default value
//                    val children = newCb.getSubClassBuilders().map { (k, cb) ->
//                        ClassBuilderNode(k.getPreviewValue(), cb, parent).toTreeItem()
//                    }
//                    item.children.addAll(children)
//                }
        }


//    /**
//     * Recalculate all child tree items recursively
//     */
//    private fun TreeItem<ClassBuilderNode>.reloadChildren(treeView: TreeView<ClassBuilderNode>) {
//        if (!value.parent.isLeaf()) {
//
//            value = value.ensurePresentClassBuilder()
//
//            //find all possible properties that can be edited
//            fun setAllChildren(item: TreeItem<ClassBuilderNode>) {
//                val childCb = item.value.cb ?: return
//                if (childCb.isLeaf()) return
//
//                println("childCB $childCb")
//
//                val childItem = childCb.toTreeItem()
//                item.parent.children.add(childItem)
//                for (_ in childCb.getTreeItems()) {
//                    setAllChildren(childItem)
//                }
//            }
//            children.clear()
//
//            for (cbn in value.cb!!.getTreeItems()) {
//                val item = cbn.toTreeItem()
//                children.add(item)
//                setAllChildren(item)
//            }
//        }
//
//        controller.currSel = value.parent.toCBN()
//        treeView.refresh()
//    }
    }
}
