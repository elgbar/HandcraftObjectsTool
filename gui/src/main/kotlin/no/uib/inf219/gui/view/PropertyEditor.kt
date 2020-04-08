package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import no.uib.inf219.extra.centeredText
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.ems
import tornadofx.*

/**
 * @author Elg
 */
class PropertyEditor : Fragment("Property Editor") {

    internal val controller: ObjectEditorController by param()

    override val root = borderpane {
        controller.tree.onUserSelect {

            center = splitpane(orientation = Orientation.VERTICAL) {
                setDividerPositions(0.25)

                this += vbox {

                    val meta = it.getPropertyMeta()

                    addClass(Styles.parent)
                    label("Required? ${meta?.required ?: false}")
                    label("Type: ${meta?.type?.rawClass ?: "Unknown"}")

                    val desc = meta?.description
                    if (!desc.isNullOrBlank()) {
                        scrollpane() {
                            style {
                                //remove the visible borders around the scroll pane
                                borderWidth = multi(box(0.ems))
                                padding = box(0.ems)
                            }
                            text("Description: $desc")
                        }
                    }
                }

                if (it.cb == null) {
                    this += borderpane {
                        center {
                            onDoubleClick {
                                controller.createSelected()
                            }
                            centeredText(
                                "Double click a property to edit it. You can also double click anywhere here to create this property.",
                                "Each property can also be reset or set to null via context menu (right click)"
                            )
                        }
                    }
                } else {
                    this += it.cb!!.createEditView(this, controller)
                }
            }
        }
    }
}

