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

package no.uib.inf219.example.gui.view

import com.fasterxml.jackson.core.type.TypeReference
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.scene.control.TabPane
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.data.SerializationManager
import no.uib.inf219.example.data.SerializationManager.kotlinJson
import no.uib.inf219.example.data.SerializationManager.readValue
import no.uib.inf219.example.data.prerequisite.AlwaysFalsePrerequisite
import no.uib.inf219.example.data.prerequisite.AlwaysTruePrerequisite
import no.uib.inf219.example.data.prerequisite.Prerequisite
import no.uib.inf219.example.data.prerequisite.logical.AndPrerequisite
import no.uib.inf219.example.gui.Main
import no.uib.inf219.example.gui.Styles
import no.uib.inf219.example.gui.Styles.Companion.ems
import no.uib.inf219.example.gui.Styles.Companion.invisibleScrollpaneBorder
import org.yaml.snakeyaml.Yaml
import tornadofx.FileChooserMode
import tornadofx.View
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.button
import tornadofx.chooseFile
import tornadofx.flowpane
import tornadofx.hbox
import tornadofx.label
import tornadofx.scrollpane
import tornadofx.separator
import tornadofx.splitpane
import tornadofx.style
import tornadofx.tab
import tornadofx.vbox

/**
 * @author Elg
 */
class SelectConversationView(val tabPane: TabPane) : View("") {

    val convs: ObservableList<Conversation> = FXCollections.observableArrayList()

    companion object {
        const val DEBUG = false
    }

    override val root = splitpane(orientation = Orientation.VERTICAL)

    init {
        with(root) {
            style {
                minWidth = 110.ems
                minHeight = 60.ems
            }

            val output: TextArea = TextArea().apply {
                isEditable = false
            }

            addClass(Styles.parent)
            vbox {
                label("Load conversations") {
                    addClass(Styles.headLineLabel)
                    addClass(Styles.parent)
                }
                separator()
                hbox {
                    addClass(Styles.parent)
                    button("Choose file") {
                        setOnAction {
                            val files = chooseFile(
                                "Choose conversations to load",
                                arrayOf(
                                    FileChooser.ExtensionFilter("JSON files", "*.json"),
                                    FileChooser.ExtensionFilter("YAML files", "*.yml", "*.yaml"),
                                    FileChooser.ExtensionFilter("All files", "*")
                                ),
                                mode = FileChooserMode.Multi
                            )
                            for (file in files) {
                                output.appendText("Loading file ${file.absolutePath}\n")
                                output.appendText("content:\n")
                                output.appendText(file.readText())
                                output.appendText("\n")

                                try {
                                    val conv: Conversation = kotlinJson.readValue(file.readText())
                                    convs += conv
                                    output.appendText("Successfully loaded conversation!")
                                } catch (e: Exception) {
                                    output.appendText("Failed to load conversation\n$e")
                                    e.printStackTrace()
                                    return@setOnAction
                                }
                            }
                        }
                    }
                    button("Clear") {
                        setOnAction {
                            output.clear()
                        }
                    }
                    if (DEBUG) {
                        button("Test generic") {
                            setOnAction {

                                val o = AndPrerequisite(listOf(AlwaysTruePrerequisite(), AlwaysTruePrerequisite()))
                                val o2 = AndPrerequisite(listOf(AlwaysTruePrerequisite(), AlwaysFalsePrerequisite(), o))

                                val dump = kotlinJson.writeValueAsString(o2)
                                output.appendText(dump)
                                output.appendText("\n\n")
                                val oread: Prerequisite

                                try {
                                    oread = kotlinJson.readValue(dump)
                                } catch (e: Exception) {
                                    output.appendText("Failed to load object back\n$e")
                                    e.printStackTrace()
                                    return@setOnAction
                                }

                                output.appendText(
                                    "\ntake 2: \n${kotlinJson.writeValueAsString(oread)}\n"
                                )
                                output.appendText("Can use ${oread::class.simpleName}? ${oread.check()}${if (!oread.check()) " (due to '${oread.reason()}')" else ""}\n")
                            }
                        }
                        button("Dump test conv") {
                            setOnAction {
                                val dump: String = kotlinJson.writeValueAsString(Main.TEST_CONV)
                                val dump2: String
                                dump2 = try {
                                    val conv: Conversation = kotlinJson.readValue(dump)
                                    convs += conv
                                    output.appendText("eql test conv obj? ${conv == Main.TEST_CONV}\n")
                                    kotlinJson.writeValueAsString(conv)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    "failed to load it back in"
                                }
                                output.appendText("eql test conv str? ${dump2 == dump}\n")
                                output.appendText("\ndump\n $dump\n\n")
                                output.appendText("dump2\n\n $dump2")
                            }
                        }
                        button("Dump end conv & exit response") {
                            setOnAction {

                                val typeref: TypeReference<List<Response>> = object : TypeReference<List<Response>>() {}

                                val exitRespDump = kotlinJson.writeValueAsString(
                                    Response.exitResponse
                                )
                                output.appendText(exitRespDump)
                                output.appendText(
                                    "\nEql when resp reload? " +
                                        "${SerializationManager.yamlMapper.readValue<List<Response>>(
                                            exitRespDump,
                                            typeref
                                        ) == Response.exitResponse}\n"
                                )

                                output.appendText(
                                    kotlinJson.writeValueAsString(
                                        kotlinJson.readValue<List<Response>>(exitRespDump)
                                    )
                                )

                                output.appendText("\n\n")

                                val endConvDump = kotlinJson.writeValueAsString(
                                    Conversation.endConversation
                                )
                                output.appendText(endConvDump)
                                output.appendText(
                                    "\nEql when conv reload? ${kotlinJson.readValue<Conversation>(
                                        endConvDump
                                    ) == Conversation.endConversation}\n"
                                )
                            }
                        }
                    }
                }
                label("Loaded conversations") {
                    addClass(Styles.headLineLabel)
                    addClass(Styles.parent)
                }
                separator()
                scrollpane(fitToHeight = true, fitToWidth = true) {
                    addClass(invisibleScrollpaneBorder)

                    flowpane {
                        addClass(Styles.parent)
                        hgap = 3.0
                        vgap = 3.0

                        bindChildren(convs) {
                            val conv = it
                            val button =
                                button(if (conv.name.isEmpty()) "Conversation #${convs.indexOf(conv)}" else conv.name)
                            with(button) {
                                setOnAction {
                                    val yaml = Yaml()
                                    output.appendText("Conversation:\n ${yaml.dump(conv)}")
                                    createTab(text, conv)
                                }
                            }
                            return@bindChildren button
                        }
                    }
                }
            }

            scrollpane(fitToHeight = true, fitToWidth = true) {
                add(output)
            }
        }
        convs.addAll(
            Main.TEST_CONV,
            Conversation.create("test")
        )
    }

    private fun createTab(name: String, conv: Conversation) {
        tabPane.tab(name, BorderPane()) {
            add(ConversationView(this, conv).root)
            tabPane.selectionModel.select(this)
        }
    }
}
