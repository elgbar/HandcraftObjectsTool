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
        val TEST_CONV = Conversation(
            "Conv Text test", "test conv",
            listOf(Response("a", "Alt a", Conversation("Sub conv!", "c2")), Response("b", "alt b"))
        )
    }
    

}
