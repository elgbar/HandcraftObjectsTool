package no.uib.inf219.example.data

import no.uib.inf219.api.serialization.Serializer

/**
 * @author Elg
 */
open class Conversation(
    val name: String,
    val text: String,
    val responses: List<Response> = listOf(ExitResponse)
) : Serializer {

    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val RESPONSE_PATH = "responses"

        fun deserialize(map: Map<String, Any?>): Conversation {
            val text = map[TEXT_PATH] as String
            val name = map[NAME_PATH] as String
            val responses = map[RESPONSE_PATH] as List<Response>
            return Conversation(name, text, responses)
        }
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[NAME_PATH] = name
        map[TEXT_PATH] = text
        return map
    }

}
