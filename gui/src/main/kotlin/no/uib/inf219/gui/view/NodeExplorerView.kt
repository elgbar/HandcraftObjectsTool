package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.MouseButton
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
    ).treeview(controller.realRoot.item) {
        controller.tree = this
        root.isExpanded = true
//        isShowRoot = false

//        repopulate()

//        populate({it.item}) {
//
//        }

        setOnMouseClicked { event ->
            //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
            if (event.clickCount == 1 && event.button == MouseButton.PRIMARY) {
                selectedValue?.ensurePresentClassBuilder()
//                refresh()
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
                val ref = selector.createReference(type, key, value!!.parent)

                if (ref == null) {
                    warning(
                        "No reference returned",
                        "No reference was returned from the search. This could be because you canceled the search (pressed escape) or because the chosen class builder was invalid."
                    )
                    return@action
                }

                //register the new reference but first null out any old reference
                value.parent.resetChild(key, restoreDefault = false)
                value.parent.createChildClassBuilder(key, ref, item)

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

        fun TreeItem<ClassBuilderNode>.resetClicked(
            treeView: TreeView<ClassBuilderNode>,
            restoreDefault: Boolean
        ) {
            //reset the clicked item
            value = value.resetClassBuilder(restoreDefault)
        }
    }
}
