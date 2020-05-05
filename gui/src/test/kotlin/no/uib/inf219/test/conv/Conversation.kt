package no.uib.inf219.test.conv

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.api.serialization.Identifiable

/**
 * TODO allow for multiple pages of text
 *
 * @author Elg
 */
class Conversation : Identifiable<String> {

    @JsonProperty("text", required = true)
    lateinit var text: String

    @JsonProperty("name", defaultValue = "\"Conversation\"")
    lateinit var name: String

    @JsonProperty("responses", defaultValue = "[]")
    var responses = ArrayList<Response>()

    /**
     * If this conversation have been held, setter always sets this to `true`
     */
    @JsonIgnore
    var hasBeenRead: Boolean = false
        set(_) {
            field = true
        }

    companion object {

        @JvmStatic
        var createId = 0
        val endConversation: Conversation =
            create(
                "(Conversation ended)",
                "End of Conversation",
                Response.exitResponse
            )

        @JvmStatic
        fun create(text: String, name: String? = null, responses: List<Response>? = null): Conversation {
            val conv = Conversation()
            conv.text = text

            if (name != null)
                conv.name = name
            if (responses != null)
                conv.responses.addAll(responses)
            return conv
        }
    }


    override fun getId(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Conversation) return false

        if (text != other.text) return false
        if (name != other.name) return false
        if (responses !== other.responses) return false
        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + responses.hashCode()
        return result
    }

    override fun toString(): String {
        return "Conversation(text='$text', name='$name', hasBeenRead=$hasBeenRead)"
    }


}
