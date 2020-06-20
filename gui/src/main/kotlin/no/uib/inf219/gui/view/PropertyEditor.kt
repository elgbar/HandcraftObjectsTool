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

                    val meta = cbn.property

                    addClass(Styles.parent)
                    label("Required? ${meta?.required}")
                    label("Expected Type: ${meta?.type?.rawClass?.typeName ?: "Unknown"}")
                    label("Real Type: ${cbn.cb?.type?.rawClass?.typeName ?: "null"}")

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
                                "This property is set to null",
                                "To create a value here double click anywhere with in editor.",
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

