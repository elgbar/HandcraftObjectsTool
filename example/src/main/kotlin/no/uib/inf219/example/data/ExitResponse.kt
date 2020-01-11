package no.uib.inf219.example.data

import kotlin.system.exitProcess

/**
 * @author Elg
 */
object ExitResponse : Response("Exit", "End conversation", Conversation("", "")) {

    override fun onSelect() {
        exitProcess(0)
    }

}
