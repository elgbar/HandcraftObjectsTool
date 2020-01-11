package no.uib.inf219.example.data

import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import no.uib.inf219.api.serialization.Serializer
import kotlin.system.exitProcess

/**
 * @author Elg
 */
class Response(
    text: String,
    val name: String = "",
    val conv: Conversation = Conversation.endConversation,
    val end: Boolean = false
) : Serializer, Button(text) {

    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val SUB_CONVERSATION_PATH = "conversation"

        fun deserialize(map: Map<String, Any?>): Response {
            val text = map[TEXT_PATH] as String
            val name = map[NAME_PATH] as String
            val conv = map[SUB_CONVERSATION_PATH] as Conversation
            return Response(text, name, conv)
        }

        val exitResponse = Response("Exit", "End conversation", Conversation("", ""), true)
    }

    fun onSelect() {
        if (end) {
            exitProcess(0)
        }
    }

    fun tooltip(): Tooltip? {
        return if (end) Tooltip("This will end the conversation") else null
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[NAME_PATH] = name
        map[TEXT_PATH] = text
        map[SUB_CONVERSATION_PATH] = conv
        return map
    }
}
