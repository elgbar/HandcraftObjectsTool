package no.uib.inf219.example.data

import no.uib.inf219.api.serialization.Serializer

/**
 * @author Elg
 */
class Response(
    val name: String,
    val text: String,
    val conversation: Conversation = EndConversation
) : Serializer {

    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val SUB_CONVERSATION_PATH = "conversation"

        fun deserialize(map: Map<String, Any?>): Response {
            val text = map[TEXT_PATH] as String
            val name = map[NAME_PATH] as String
            val conv = map[SUB_CONVERSATION_PATH] as Conversation
            return Response(name, text, conv)
        }
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[NAME_PATH] = name
        map[TEXT_PATH] = text
        map[SUB_CONVERSATION_PATH] = conversation
        return map
    }
}
