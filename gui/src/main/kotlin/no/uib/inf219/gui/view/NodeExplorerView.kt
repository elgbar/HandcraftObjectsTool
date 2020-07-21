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

import javafx.scene.control.ButtonType
import javafx.scene.input.KeyCode
import javafx.util.Duration
import no.uib.inf219.extra.YES_DISABLE_WARNING
import no.uib.inf219.extra.reload
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Settings.showOverwriteWithRefWarning
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.select.ReferenceSelectorView
import tornadofx.FX
import tornadofx.View
import tornadofx.action
import tornadofx.cellFormat
import tornadofx.checkmenuitem
import tornadofx.confirmation
import tornadofx.contextmenu
import tornadofx.information
import tornadofx.item
import tornadofx.runLater
import tornadofx.scrollpane
import tornadofx.selectedValue
import tornadofx.tooltip
import tornadofx.treeview
import tornadofx.warning

/**
 * @author Elg
 */
class NodeExplorerView(private val controller: ObjectEditorController) : View("Tree Explorer") {

    override val root = scrollpane(
        fitToWidth = true,
        fitToHeight = true
    ).treeview(controller.root.item) {
        controller.tree = this
        root.isExpanded = true

        setOnKeyPressed { event ->
            // note that "isPrimaryButtonDown" and "isSecondaryButtonDown" does not work
            if (selectedValue?.cb == null && (event.code == KeyCode.ENTER || event.code == KeyCode.SPACE)) {
                controller.createSelected()
                return@setOnKeyPressed
            }
            if (selectedValue !== root) {
                selectedValue?.cb?.onNodeKeyEvent(event, controller)
            }
        }

        cellFormat { cbn ->
            runLater(Duration.millis(1.0)) {
                // to not update straight away as items might still be removed
                text = cbn.key.getPreviewValue()
                tooltip("Class: ${cbn.type}")
                this.contextMenu = null
                cbn.item.expandedProperty().addListener { _, _, newValue ->
                    if (!newValue && Settings.collapseChildren == true) {
                        cbn.item.children.forEach { it.isExpanded = false }
                    }
                }
                contextmenu {
                    if (cbn.item !== root) {

                        val refItem = item("Make reference to...").apply {
                            if (cbn.allowReference) {
                                action {

                                    val (key, cb, parent) = cbn

                                    val type = parent.getChildType(key)
                                    if (type == null) {
                                        information(
                                            "Failed to find a the type of the child $key for $parent",
                                            owner = FX.primaryStage
                                        )
                                        return@action
                                    }

                                    if (cb != null && showOverwriteWithRefWarning != false) {
                                        confirmation(
                                            "Do you want to overwrite it with a reference to another object?",
                                            "This property is already defined: $cb", owner = FX.primaryStage,
                                            buttons = *arrayOf(ButtonType.YES, YES_DISABLE_WARNING, ButtonType.CANCEL),
                                            actionFn = { button ->
                                                // hitting esc/closing window also counts as cancel
                                                if (button == ButtonType.CANCEL) {
                                                    return@action
                                                } else if (button == YES_DISABLE_WARNING) {
                                                    showOverwriteWithRefWarning = false
                                                }
                                                // ButtonType.YES falls through
                                            }
                                        )
                                    }

                                    val selector: ReferenceSelectorView = find("controller" to controller)
                                    val ref = selector.createReference(type, key, parent)

                                    if (ref == null) {
                                        warning(
                                            "No reference returned",
                                            "No reference was returned from the search. This could be because you canceled the search (pressed escape) or because the chosen class builder was invalid.", owner = FX.primaryStage
                                        )
                                        return@action
                                    }

                                    parent[key] = ref

                                    cbn.item.children.clear()
                                    reload()
                                }
                            } else {
                                isDisable = true
                            }
                        }
                        val coll = this.checkmenuitem("Collapse All Children") {
                            selectedProperty().addListener { _, _, newValue -> Settings.collapseChildren = newValue }
                            isSelected = Settings.collapseChildren!!
                        }

                        setOnHiding {
                            this.items.setAll(refItem, coll)
                        }

                        setOnShowing {
                            cbn.parent.createChildContextItems(cbn.key, this, controller)
                        }
                    }
                }
            }
        }
    }
}
