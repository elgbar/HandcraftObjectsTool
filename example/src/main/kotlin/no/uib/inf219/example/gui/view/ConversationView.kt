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

import javafx.scene.control.Tab
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.gui.Main
import no.uib.inf219.example.gui.Styles
import no.uib.inf219.example.gui.Styles.Companion.conversationBorderPane
import no.uib.inf219.example.gui.Styles.Companion.responseHBox
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.button
import tornadofx.clear
import tornadofx.close
import tornadofx.hbox
import tornadofx.label

/**
 * @author Elg
 */
class ConversationView(val tab: Tab, var conv: Conversation) : View() {

    override val root = borderpane {
        Main.title.value = conv.name
        addClass(conversationBorderPane)

        setText(this, conv.text)
        bottom {
            hbox {
                addClass(responseHBox)
                conv.hasBeenRead = true
                createButtons(conv.responses, this)
            }
        }
    }

    private fun createButtons(resps: List<Response>, parent: HBox) {
        with(parent) {
            clear()

            for (response in resps) {
                with(button(response.response)) {
                    this.disableProperty().set(!(response.prereq?.check() ?: true))
                    setOnAction {
                        if (response.prereq?.check() != false) {
                            if (response.shouldClose()) {
                                tab.close()
                                return@setOnAction
                            }
                            conv = response.conv!!
                            conv.hasBeenRead = true
                            setText(root, conv.text)
                            createButtons(conv.responses, parent)
                        }
                    }
                    tooltip = response.tooltip()
                    addClass(Styles.responseButton)
                }
            }
        }
    }

    private fun setText(parent: BorderPane, text: String) {
        with(parent) {
            top = label(text) {
                addClass(Styles.conversationLabel)
            }
        }
    }
}
