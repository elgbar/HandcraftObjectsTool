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

            SerializationManager.registerConfigurationSerializers("no.uib.inf219.example")

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
        }

        val TEST_CONV = Conversation.create(
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
                //FIXME allow for recursion
                , Response.create("Can you teach me about recursion?", conv = recursionConv)
            )
        )
    }


}
