package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.text.TextAlignment
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.ems
import tornadofx.*

/**
 * @author Elg
 */
class PropertyEditor : Fragment("Attribute Editor") {

    internal val controller: ObjectEditorController by param()

    override val root = borderpane {
        controller.tree.onUserSelect {
            val cb = it.cb
            if (cb == null) {
                center {
                    vbox {
                        alignment = Pos.CENTER
                        textflow {
                            textAlignment = TextAlignment.CENTER

                            text("Select a property to edit it.")
                            text("Each property can also be reset or set to null via context menu (right click)")
                        }
                    }
                }
            } else {

                center = splitpane(orientation = Orientation.VERTICAL) {
                    setDividerPositions(0.25)

                    this += vbox {
                        addClass(Styles.parent)
                        label("Required? ${cb.isRequired()}")
                        label("Type: ${cb.type.rawClass}")

                        val desc = cb.property?.description
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
                    this += cb.toView(this, controller)
                }
            }
        }
    }
}
