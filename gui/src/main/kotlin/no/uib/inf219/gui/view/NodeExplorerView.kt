package no.uib.inf219.gui.view

import javafx.scene.input.MouseButton
import no.uib.inf219.extra.selectedItem
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.ObjectEditorController.Companion.reload
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

        setOnMouseClicked { event ->
            //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
            if (event.clickCount == 1 && event.button == MouseButton.PRIMARY) {
                selectedItem?.value = selectedValue?.ensurePresentClassBuilder(this)
            }
        }

        cellFormat {
            text = it.key.getPreviewValue()
            tooltip("Class: ${it.cb?.type ?: it.parent.getChildType(it.key)}")
        }

        contextmenu {

            item("Make reference to...").action {
                val item = this@treeview.selectionModel.selectedItem ?: return@action
                if (item == root) {
                    warning("Cannot overwrite root with a reference")
                    return@action
                }
                val value = item.value

                val key = value.key
                val parent = value.parent

                val type = parent.getChildType(key)
                if (type == null) {
                    information("Failed to find a the type of the child $key for $parent")
                    return@action
                }

                val selector: ReferenceSelectorView = find("controller" to controller)
                val ref = selector.createReference(type, key, parent)

                if (ref == null) {
                    warning(
                        "No reference returned",
                        "No reference was returned from the search. This could be because you canceled the search (pressed escape) or because the chosen class builder was invalid."
                    )
                    return@action
                }

                parent[key] = ref
                reload()
            }

            item("Restore to default").action {
                this@treeview.selectionModel.selectedItem?.value?.resetClassBuilder(this@treeview, true)
            }

            item("Set to null").action {
                this@treeview.selectionModel.selectedItem?.value?.resetClassBuilder(this@treeview, false)
            }
        }
    }
}
