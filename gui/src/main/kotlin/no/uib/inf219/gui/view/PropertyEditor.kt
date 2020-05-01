package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.scene.input.KeyCode
import no.uib.inf219.extra.centeredText
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * @author Elg
 */
class PropertyEditor : Fragment("Property Editor") {

    internal val controller: ObjectEditorController by param()

    override val root = borderpane {
        controller.tree.onUserSelect { cbn ->

            center = splitpane(orientation = Orientation.VERTICAL) {
                setDividerPositions(0.0)

                this += vbox {

                    val meta = cbn.getPropertyMeta()

                    addClass(Styles.parent)
                    label("Required? ${meta?.required ?: false}")
                    label("Expected Type: ${meta?.type?.rawClass ?: "Unknown"}")
                    label("Real Type: ${cbn.cb?.type?.rawClass ?: "null"}")

                    val desc = meta?.description
                    if (!desc.isNullOrBlank()) {
                        scrollpane() {
                            addClass(Styles.invisibleScrollpaneBorder)
                            text("Description: $desc")
                        }
                    }
                }

                if (cbn.cb == null) {
                    this += borderpane {
                        center {
                            onDoubleClick {
                                controller.createSelected()
                            }
                            setOnKeyPressed { event ->
                                if ((event.code == KeyCode.ENTER || event.code == KeyCode.SPACE)) {
                                    controller.createSelected()
                                }
                            }
                            centeredText(
                                "Double click a property to edit it. You can also double click anywhere here to create this property.",
                                "Each property can also be reset or set to null via context menu (right click)"
                            )
                        }
                    }
                } else {
                    this += cbn.cb!!.createEditView(this, controller)
                }
            }
        }
    }
}

