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

package no.uib.inf219.example.gui

import javafx.beans.property.StringProperty
import javafx.scene.Scene
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.data.prerequisite.ReadConversationPrerequisite
import no.uib.inf219.example.gui.view.BackgroundView
import no.uib.inf219.example.storage.StoreHandler
import tornadofx.App
import tornadofx.UIComponent

/**
 * @author Elg
 */
class Main : App(BackgroundView::class, Styles::class) {

    override fun createPrimaryScene(view: UIComponent): Scene {
        title = view.titleProperty
        return super.createPrimaryScene(view)
    }

    companion object {

        lateinit var title: StringProperty
            private set

        private val recursionConv: Conversation

        private const val aConvName = "What this?"
        val TEST_CONV: Conversation

        init {

            val respList = ArrayList<Response>()
            recursionConv = Conversation.create("Do you know what recursion is?", responses = respList)
            respList += Response.create(
                "No, I do not", conv =
                Conversation.create(
                    "Well I will teach you", responses = mutableListOf(
                        Response.create("Okay great!", conv = recursionConv)
                    )
                )
            )
            respList += Response.create(
                "Yes, I do", conv = Conversation.create(
                    "You now know all there is about recursion"
                )
            )

            TEST_CONV = Conversation.create(
                "Welcome to this conversation!",
                "Hard coded conversation",
                mutableListOf(
                    Response.create(
                        "That's a weird thing to say",
                        conv = Conversation.create(
                            "Yes it is, this is just an example though. I do need to write something to fill this example.",
                            responses = mutableListOf(
                                Response.create("I suppose you do")
                            )
                        )
                    ),
                    Response.create(
                        "Wait, what is this?",
                        conv = Conversation.create(
                            "This is an example application, it's just a way to show off what HOT can be used for",
                            name = aConvName,
                            responses = mutableListOf(
                                Response.create(
                                    "So whats HOT then?",
                                    conv = Conversation.create(
                                        "HOT (or Handcraft Objects Tool) is an interactive way to create JVM objects, like this conversation.",
                                        responses = mutableListOf(
                                            Response.create(
                                                "So this is made in HOT?",
                                                conv = Conversation.create(
                                                    "Well, err no. This is just hardcoded in",
                                                    responses = mutableListOf(
                                                        Response.create(
                                                            "Oh well that is disappointing..."
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
            TEST_CONV.responses.add(
                Response.create(
                    "Can you teach me about recursion?",
                    conv = recursionConv,
                    prereq = ReadConversationPrerequisite(
                        StoreHandler.getStore<String, Conversation>(Conversation::class.java).retrieve(aConvName)
                    )
                )
            )
        }
    }
}
