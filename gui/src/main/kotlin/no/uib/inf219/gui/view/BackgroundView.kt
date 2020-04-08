package no.uib.inf219.gui.view

import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.StageStyle
import no.uib.inf219.extra.applicationHome
import no.uib.inf219.extra.centeredText
import no.uib.inf219.extra.close
import no.uib.inf219.extra.closeAll
import no.uib.inf219.extra.internetHyperlink
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.ems
import tornadofx.*

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
            tab(CONTROL_PANEL_TAB_NAME, BorderPane()) {
                this += ControlPanelView
                this.isClosable = false
            }
        }

        with(root) {
            top = menubar {
                menu("File") {
                    item("Close Tab", "Ctrl+W").action {

                        val tab = tabPane.selectionModel.selectedItem
                        if (tab.isClosable) {
                            tab.close()
                        }
                    }
                    item("Close All Tab", "Ctrl+Shift+W").action {
                        tabPane.closeAll()
                    }

                    separator()

                    item("Save", "Ctrl+S").action {
                        val tab = tabPane.selectionModel.selectedItem
                        if (tab.text == CONTROL_PANEL_TAB_NAME) return@action //cannot save control panel
                        val oebv = ControlPanelView.tabMap[tab] ?: return@action

                        oebv.save()
                    }
                    separator()
                    item("Settings").action {

                        object : View("Application Settings") {
                            override val root = vbox {
                                addClass(Styles.parent)
                                button("Reset settings").action {
                                    applicationHome().deleteRecursively()
                                }
                            }
                        }.openModal(block = true, owner = currentWindow)
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
                                        "Licenced under Apache License 2.0"
                                    ) {
                                        addClass(Styles.parent)
                                        internetHyperlink(
                                            "HandcraftObjectsTool on GitHub",
                                            "https://github.com/kh498/HandcraftObjectsTool"
                                        )

                                        separator()

                                        textflow {
                                            text("Software, tools, and graphics used\n") {
                                                style {
                                                    fontSize = 1.5.ems
                                                }
                                            }
                                            text("Jackson, licenced under Apache License 2.0\n")
                                            text("TornadoFx, licenced under Apache License 2.0\n")
                                            text("Apache Commons Text, licenced under Apache License 2.0\n")
                                            text("ClassGraph, licenced under the MIT License\n")
                                            text("SLF4J source code and binaries are distributed under the MIT license\n")
                                            text("Ubuntu font, licenced under UBUNTU FONT LICENCE\n")
                                            text("Icon, licenced under Pixabay License\n")
                                        }
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
                this += OutputArea
            }
        }
    }
}
