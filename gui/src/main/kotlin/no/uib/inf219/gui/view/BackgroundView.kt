package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import javafx.stage.StageStyle
import no.uib.inf219.extra.*
import no.uib.inf219.extra.close
import no.uib.inf219.gui.Settings
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.ems
import org.controlsfx.control.PropertySheet
import tornadofx.*
import tornadofx.controlsfx.action
import tornadofx.controlsfx.hyperlinklabel
import tornadofx.controlsfx.propertysheet
import kotlin.system.exitProcess

/**
 * @author Elg
 */
class BackgroundView : View("Handcrafted Objects Tool") {

    val tabPane: TabPane

    override val root = borderpane()

    companion object {
        const val CONTROL_PANEL_TAB_NAME = "Control Panel"
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
                            tabPane.selectionModel.selectedItemProperty().booleanBinding() { it?.isClosable ?: true }
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
                            tabPane.tabs.sizeProperty.booleanBinding() { (it as Int) > 1 }
                        }
                        action { tabPane.closeAll() }
                    }

                    separator()

                    item("Save", "Ctrl+S") {
                        enableWhen {
                            tabPane.selectionModel.selectedItemProperty().booleanBinding() { it?.isClosable ?: true }
                        }
                        action {
                            val tab = tabPane.selectionModel.selectedItem
                            if (tab.text == CONTROL_PANEL_TAB_NAME) return@action //cannot save control panel
                            val oebv = ControlPanelView.tabMap[tab] ?: return@action

                            oebv.save()
                        }
                    }
                    separator()
                    item("Settings", "Ctrl+Alt+S").action {

                        object : View("Application Settings") {
                            override val root = vbox {
                                addClass(Styles.parent)

                                propertysheet(Settings, mode = PropertySheet.Mode.NAME)

                                separator()

                                button("Reset All Settings").action {
                                    tooltip("A restart is required for the changes to take effect")
                                    applicationHome().deleteRecursively()
                                }
                            }
                        }.openModal(block = true, owner = currentWindow)
                    }
                    item("Clear logs").action { LoggerView.clear() }
                    item("Exit").action { exitProcess(0) }
                }

                menu("Build") {

                    //use Ctrl+D as Ctrl+V is used for pasting when in an editor
                    item("Validate", "Ctrl+D") {
                        enableWhen {
                            tabPane.selectionModel.selectedItemProperty().booleanBinding() { it?.isClosable ?: true }
                        }
                        action {
                            val tab = tabPane.selectionModel.selectedItem
                            if (tab.text == CONTROL_PANEL_TAB_NAME) return@action //cannot save control panel
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
                                        "Author: Karl Henrik Elg Barlinn",
                                        "Licenced under Apache License 2.0",
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
                                            "[Jackson], licenced under [Apache License 2.0]",
                                            "Jackson" to "https://github.com/FasterXML/jackson",
                                            "Apache License 2.0" to "http://www.apache.org/licenses/LICENSE-2.0"
                                        )

                                        hyperWeblinkLabel(
                                            "[TornadoFx], licenced under [Apache License 2.0]",
                                            "TornadoFx" to "https://github.com/edvin/tornadofx",
                                            "Apache License 2.0" to "https://github.com/edvin/tornadofx/blob/master/LICENSE"
                                        )

                                        hyperWeblinkLabel(
                                            "[ClassGraph], licenced under the [MIT license]",
                                            "ClassGraph" to "https://github.com/classgraph/classgraph",
                                            "MIT license" to "https://github.com/classgraph/classgraph/blob/master/LICENSE-ClassGraph.txt"
                                        )

                                        hyperWeblinkLabel(
                                            "[Apache Commons Text], licenced under [Apache License 2.0]",
                                            "Apache Commons Text" to "https://commons.apache.org/proper/commons-text/",
                                            "Apache License 2.0" to "https://www.apache.org/licenses/LICENSE-2.0"
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
                                            "[TornadoFX-ControlsFX], licenced under [Apache License 2.0]",
                                            "TornadoFX-ControlsFX" to "https://github.com/edvin/tornadofx-controlsfx",
                                            "Apache License 2.0" to "https://github.com/edvin/tornadofx-controlsfx/blob/master/LICENSE"
                                        )
                                        hyperWeblinkLabel(
                                            "[JavaWuzzy], licenced under [GNU General Public License v2.0]",
                                            "JavaWuzzy" to "https://github.com/xdrop/fuzzywuzzy",
                                            "GNU General Public License v2.0" to "https://github.com/xdrop/fuzzywuzzy/blob/master/LICENSE"
                                        )
                                        hyperWeblinkLabel(
                                            "[Kotlin events], licenced under the [MIT License]",
                                            "Kotlin events" to "https://github.com/stuhlmeier/kotlin-events",
                                            "MIT License" to "https://github.com/stuhlmeier/kotlin-events/blob/master/LICENSE"
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
