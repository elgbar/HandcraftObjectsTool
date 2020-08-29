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
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.StageStyle
import no.uib.inf219.extra.Persistent
import no.uib.inf219.extra.centeredText
import no.uib.inf219.extra.close
import no.uib.inf219.extra.closeAll
import no.uib.inf219.extra.internetHyperlink
import no.uib.inf219.extra.openWebPage
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.ems
import org.controlsfx.control.PropertySheet
import tornadofx.*
import tornadofx.controlsfx.action
import tornadofx.controlsfx.hyperlinklabel
import tornadofx.controlsfx.propertysheet
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.system.exitProcess

/**
 * @author Elg
 */
class BackgroundView : View("Handcrafted Objects Tool") {

    val tabPane: TabPane

    override val root = borderpane()

    companion object {
        const val CONTROL_PANEL_TAB_NAME = "Control Panel"
        const val APACHE_2_0_LICENSE = "Apache License 2.0"
        const val MIT_LICENSE = "MIT license"
    }

    init {
        tabPane = tabpane {
            tabMaxHeight = Double.MAX_VALUE
            tab(CONTROL_PANEL_TAB_NAME, BorderPane()) {
                this += ControlPanelView
                this.isClosable = false
            }
        }

        with(root) {
            top = menubar {
                menu("File") {
                    item("Close Tab", "Ctrl+W") {
                        enableWhen {
                            tabPane.selectionModel.selectedItemProperty().booleanBinding { it?.isClosable ?: true }
                        }
                        action {
                            val tab = tabPane.selectionModel.selectedItem
                            if (tab.isClosable) {
                                tab.close()
                            }
                        }
                    }
                    item("Close All Tab", "Ctrl+Shift+W") {
                        enableWhen {
                            tabPane.tabs.sizeProperty.booleanBinding { (it as Int) > 1 }
                        }
                        action { tabPane.closeAll() }
                    }

                    separator()

                    item("Save", "Ctrl+S") {
                        enableWhen {
                            tabPane.selectionModel.selectedItemProperty().booleanBinding { it?.isClosable ?: true }
                        }
                        action {
                            val tab = tabPane.selectionModel.selectedItem
                            if (tab.text == CONTROL_PANEL_TAB_NAME) return@action // cannot save control panel
                            val oebv = ControlPanelView.tabMap[tab] ?: return@action

                            oebv.save()
                        }
                    }
                    separator()
                    item("Settings", "Ctrl+Alt+S") {

                        fun openSettings() {
                            object : View("Application Settings") {
                                val view: View = this

                                override val root = vbox {
                                    addClass(Styles.parent)

                                    propertysheet(Settings, mode = PropertySheet.Mode.NAME)

                                    separator()

                                    button("Reset All Settings") {
                                        action {

                                            Settings::class.declaredMemberProperties.map {
                                                it.isAccessible = true
                                                val delegate = it.getDelegate(Settings)
                                                if (delegate is Persistent<*>) {
                                                    delegate.resetValue(Settings, it)
                                                }
                                            }

                                            val folderFile = Persistent.persistentFolderFile
                                            if (!folderFile.deleteRecursively()) {
                                                LoggerView.log { "Failed to delete ${folderFile.name}. Will try to delete it on exit." }
                                                folderFile.deleteOnExit()
                                            }

                                            // reopen this view to update settings values
                                            view.close()
                                            runLater {
                                                openSettings()
                                            }
                                        }
                                    }
                                }
                            }.openModal(block = true, owner = currentWindow)
                        }

                        action { openSettings() }
                    }
                    item("Clear logs").action { LoggerView.clear() }
                    item("Exit").action { exitProcess(0) }
                }

                menu("Build") {

                    // use Ctrl+D as Ctrl+V is used for pasting when in an editor
                    item("Validate", "Ctrl+D") {
                        enableWhen {
                            tabPane.selectionModel.selectedItemProperty().booleanBinding { it?.isClosable ?: true }
                        }
                        action {
                            val tab = tabPane.selectionModel.selectedItem
                            if (tab.text == CONTROL_PANEL_TAB_NAME) return@action // cannot save control panel
                            val oebv = ControlPanelView.tabMap[tab] ?: return@action

                            oebv.validate()
                        }
                    }
                }

                menu("Help") {
                    item("About").action {

                        object : View("About HOT") {
                            override val root = borderpane {

                                style {
                                    backgroundColor = multi(Color.WHITE)
                                }
                                addClass(Styles.parent)

                                top {
                                    centeredText("Handcraft Objects Tool") {
                                        addClass(Styles.headLineLabel)
                                    }
                                }

                                center {
                                    centeredText(
                                        "Open source tool to create JVM objects",
                                        "",
                                        "Version 1.1.2",
                                        "Author: Karl Henrik Elg Barlinn",
                                        "Licenced under $APACHE_2_0_LICENSE",
                                        textAlignment = TextAlignment.CENTER
                                    ) {
                                        addClass(Styles.parent)
                                        internetHyperlink(
                                            "HandcraftObjectsTool on GitHub",
                                            "https://github.com/kh498/HandcraftObjectsTool"
                                        )

                                        label("Open Source Resources used\n") {
                                            style {
                                                fontSize = 1.5.ems
                                            }
                                        }
                                        separator()

                                        fun hyperWeblinkLabel(text: String, vararg links: Pair<String, String>) {
                                            hyperlinklabel(text).action {
                                                @Suppress("LABEL_NAME_CLASH") val url =
                                                    links.toMap()[this.text] ?: return@action
                                                openWebPage(url)
                                            }
                                        }

                                        hyperWeblinkLabel(
                                            "[Jackson], licenced under [$APACHE_2_0_LICENSE]",
                                            "Jackson" to "https://github.com/FasterXML/jackson",
                                            APACHE_2_0_LICENSE to "http://www.apache.org/licenses/LICENSE-2.0"
                                        )

                                        hyperWeblinkLabel(
                                            "[TornadoFx], licenced under [$APACHE_2_0_LICENSE]",
                                            "TornadoFx" to "https://github.com/edvin/tornadofx",
                                            APACHE_2_0_LICENSE to "https://github.com/edvin/tornadofx/blob/master/LICENSE"
                                        )

                                        hyperWeblinkLabel(
                                            "[ClassGraph], licenced under the [$MIT_LICENSE]",
                                            "ClassGraph" to "https://github.com/classgraph/classgraph",
                                            MIT_LICENSE to "https://github.com/classgraph/classgraph/blob/master/LICENSE-ClassGraph.txt"
                                        )

                                        hyperWeblinkLabel(
                                            "[Apache Commons Text], licenced under [$APACHE_2_0_LICENSE]",
                                            "Apache Commons Text" to "https://commons.apache.org/proper/commons-text/",
                                            APACHE_2_0_LICENSE to "https://www.apache.org/licenses/LICENSE-2.0"
                                        )

                                        hyperWeblinkLabel(
                                            "[Ubuntu fonts], licenced under [UBUNTU FONT LICENCE Version 1.0]",
                                            "Ubuntu fonts" to "https://design.ubuntu.com/font/",
                                            "UBUNTU FONT LICENCE Version 1.0" to "https://ubuntu.com/legal/font-licence"
                                        )

                                        hyperWeblinkLabel(
                                            "[Chicle Icon], licenced under [Pixabay License]",
                                            "Chicle Icon" to "https://pixabay.com/vectors/blade-chisel-gouge-sculpture-tool-2027204/",
                                            "Pixabay License" to "https://pixabay.com/service/license/"
                                        )

                                        hyperWeblinkLabel(
                                            "[ControlsFX], licenced under [BSD 3-Clause License]",
                                            "ControlsFX" to "https://github.com/controlsfx/controlsfx",
                                            "BSD 3-Clause License" to "https://github.com/controlsfx/controlsfx/blob/master/license.txt"
                                        )

                                        hyperWeblinkLabel(
                                            "[TornadoFX-ControlsFX], licenced under [$APACHE_2_0_LICENSE]",
                                            "TornadoFX-ControlsFX" to "https://github.com/edvin/tornadofx-controlsfx",
                                            APACHE_2_0_LICENSE to "https://github.com/edvin/tornadofx-controlsfx/blob/master/LICENSE"
                                        )
                                        hyperWeblinkLabel(
                                            "[JavaWuzzy], licenced under [GNU General Public License v2.0]",
                                            "JavaWuzzy" to "https://github.com/xdrop/fuzzywuzzy",
                                            "GNU General Public License v2.0" to "https://github.com/xdrop/fuzzywuzzy/blob/master/LICENSE"
                                        )
                                        hyperWeblinkLabel(
                                            "[Kotlin events], licenced under the [$MIT_LICENSE]",
                                            "Kotlin events" to "https://github.com/stuhlmeier/kotlin-events",
                                            MIT_LICENSE to "https://github.com/stuhlmeier/kotlin-events/blob/master/LICENSE"
                                        )
                                    }
                                }
                            }
                        }.openModal(
                            block = true,
                            owner = currentWindow,
                            resizable = false,
                            stageStyle = StageStyle.UNIFIED
                        )
                    }
                }
            }
            center = splitpane(orientation = Orientation.VERTICAL) {
                style {
                    minWidth = 110.ems
                    minHeight = 60.ems
                }
                setDividerPositions(0.75)
                this += tabPane
                this += LoggerView
            }
        }
    }
}
