/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.scene.input.KeyCode
import no.uib.inf219.extra.centeredText
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.center
import tornadofx.label
import tornadofx.onDoubleClick
import tornadofx.onUserSelect
import tornadofx.plusAssign
import tornadofx.scrollpane
import tornadofx.splitpane
import tornadofx.text
import tornadofx.vbox

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
                        scrollpane {
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
