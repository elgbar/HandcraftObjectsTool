package no.uib.inf219.gui.view

import javafx.scene.control.ButtonType
import javafx.scene.input.MouseButton
import no.uib.inf219.extra.OK_DISABLE_WARNING
import no.uib.inf219.extra.Persistent
import no.uib.inf219.extra.reload
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(private val controller: ObjectEditorController) : View("Tree Explorer") {

    /**
     * If a warning should be displayed when overwriting a property with a reference
     */
    private var warnWhenOverwriteRef by Persistent(true)

    override val root = scrollpane(
        fitToWidth = true,
        fitToHeight = true
    ).treeview(controller.realRoot.item) {
        controller.tree = this
        root.isExpanded = true

        setOnMouseClicked { event ->
            //note that "isPrimaryButtonDown" and "isSecondaryButtonDown" is not used as it does not work
            if (event.clickCount == 2 && event.button == MouseButton.PRIMARY) {
                controller.createSelected()
            }
            selectedValue?.cb?.onNodeClick(event, controller)
        }

        cellFormat {
            text = it.key.getPreviewValue()
            tooltip("Class: ${it.cb?.type ?: it.parent.getChildType(it.key)}")

            this.contextMenu = null
            contextmenu {
                if (it.item !== root) {
                    it.parent.createChildContextItems(it.key, this, controller)

                    item("Make reference to...").action {

                        val (key, cb, parent) = it

                        val type = parent.getChildType(key)
                        if (type == null) {
                            information("Failed to find a the type of the child $key for $parent")
                            return@action
                        }

                        if (cb != null && warnWhenOverwriteRef != false) {
                            warning(
                                "Do you want to overwrite it with a reference to another object?",
                                "This property is already defined: $cb",
                                owner = currentWindow,
                                buttons = *arrayOf(ButtonType.OK, OK_DISABLE_WARNING, ButtonType.CANCEL),
                                actionFn = { button ->
                                    //hitting esc/closing window also counts as cancel
                                    if (button == ButtonType.CANCEL) {
                                        return@action
                                    } else if (button == OK_DISABLE_WARNING) {
                                        warnWhenOverwriteRef = false
                                    }
                                }
                            )
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
                }
            }
        }


    }
}
