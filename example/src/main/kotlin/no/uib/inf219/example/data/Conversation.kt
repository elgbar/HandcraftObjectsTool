package no.uib.inf219.example.data

import no.uib.inf219.api.serialization.Serializable

/**
 * TODO allow for multiple pages of text
 *
 * @author Elg
 */
class Conversation(
    val text: String,
    val name: String = "Conversation #$convCount",
    val responses: List<Response> = listOf(Response.exitResponse)
) : Serializable {


    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val RESPONSE_PATH = "responses"

        private var convCount = 0

        init {
            convCount++
        }

        @JvmStatic
        fun deserialize(map: Map<String, Any?>): Conversation {
            val text = map[TEXT_PATH] as String
            val name = map[NAME_PATH] as String? ?: ""
            val responses = ArrayList<Response>()
            for (any in map[RESPONSE_PATH] as List<*>) {
                when {
                    any is Response -> {
                        responses += any
                    }
                    any != null -> {
                        throw IllegalArgumentException("One of the responses is not a response but a ${any::class.simpleName}")
                    }
                    else -> {
                        throw IllegalArgumentException("One of the responses is null")
                    }
                }
            }

            return Conversation(text, name, responses)
        }

        val endConversation = Conversation(
            "(Conversation ended)",
            "End of Conversation",
            listOf(Response("End conversation", end = true, conv = Conversation("")))
        )
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[TEXT_PATH] = text
        if (name.isNotEmpty())
            map[NAME_PATH] = name
        if (responses != listOf(Response.exitResponse))
            map[RESPONSE_PATH] = responses
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Conversation) return false

        if (text != other.text) return false
        if (name != other.name) return false
        if (responses != other.responses) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + responses.hashCode()
        return result
    }


}
