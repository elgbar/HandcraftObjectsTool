package no.uib.inf219.example.gui

import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.data.Response
import no.uib.inf219.example.gui.view.BackgroundView
import tornadofx.App

/**
 * @author Elg
 */
class Main : App(BackgroundView::class, Styles::class) {

    companion object {

        private val recursionConv: Conversation

        init {
            SerializationManager.registerConfigurationSerializers(
                "no.uib.inf219.example"
            )

            val respList = ArrayList<Response>()
            recursionConv = Conversation("Do you know what recursion is?", responses = respList)
            respList += Response(
                "No, I do not", conv =
                Conversation(
                    "Well I will teach you", responses = listOf(
                        Response("Okay great!", conv = recursionConv)
                    )
                )
            )
            respList += Response(
                "Yes, I do", conv = Conversation(
                    "You now know all there is about recursion"
                )
            )
        }

        val TEST_CONV = Conversation(
            "Welcome to this conversation!",
            responses = listOf(
                Response(
                    "That's a weird thing to say",
                    conv = Conversation(
                        "Yes it is, this is just an example though. I do need to write something to fill this example.",
                        responses = listOf(
                            Response("I suppose you do", end = true)
                        )
                    )
                ),
                Response(
                    "Wait, what is this?",
                    conv = Conversation(
                        "This is an example application, it's just a way to show off what HOT can be used for",
                        responses = listOf(
                            Response(
                                "So whats HOT then?",
                                conv = Conversation(
                                    "HOT (or Handcraft Objects Tool) is an interactive way to create JVM objects, like this conversation.",
                                    responses = listOf(
                                        Response(
                                            "So this is made in HOT?",
                                            conv = Conversation(
                                                "Well, err no. This is just hardcoded in",
                                                responses = listOf(
                                                    Response(
                                                        "Oh well that is disappointing...",
                                                        end = true
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ), Response("Can you teach me about recursion?", conv = recursionConv)
            )
        )
    }


}
