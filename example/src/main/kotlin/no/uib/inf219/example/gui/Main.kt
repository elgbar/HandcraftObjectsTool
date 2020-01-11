package no.uib.inf219.example.gui

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
            val respList = ArrayList<Response>()
            recursionConv = Conversation("recursion", "Do you know what recursion is?", respList)
            respList += Response(
                "recNo", "No, I do not",
                Conversation(
                    "recNow", "Well I will teach you", listOf(
                        Response("g", "Okay great!", recursionConv)
                    )
                )
            )
            respList += Response("recYes", "Yes, I do")
        }


        val TEST_CONV = Conversation(
            "Intro", "Welcome to this conversation!",
            listOf(
                Response(
                    "Weird intro response", "That's a weird thing to say",
                    Conversation(
                        "yes", "Yes it is, this is just an example though. I need to write something.", listOf(
                            Response("yes2", "I suppose it is")
                        )
                    )
                ),
                Response(
                    "confused intro response", "Wait, what is this?",
                    Conversation(
                        "example",
                        "This is an example application, it's just a way to show off what HOT can be used for",
                        listOf(
                            Response(
                                "hot", "So whats HOT then?",
                                Conversation(
                                    "so hot",
                                    "HOT (or Handcraft Objects Tool) is an interactive way to create JVM objects, Like this conversation.",
                                    listOf(
                                        Response(
                                            "que confusion", "So this is made in HOT?",
                                            Conversation(
                                                "oops",
                                                "Well, err no. This is just hardcoded in"
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ), Response("rec", "Can you teach me about recursion?", recursionConv)
            )
        )
    }


}
