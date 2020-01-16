package no.uib.inf219.example.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * TODO allow for multiple pages of text
 *
 * @author Elg
 */
//@JsonIdentityInfo(
//    generator = ObjectIdGenerators.IntSequenceGenerator::class,
//    scope = Conversation::class
//)
//@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator::class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class Conversation(
    @JsonProperty("text", required = true)
    val text: String,

//    @JsonManagedReference
    @JsonProperty("name", defaultValue = "Conversation")
    val name: String = "Conversation #?",

    @JsonProperty("responses", defaultValue = "[]")
    responses: List<Response> = ArrayList()
) {

    val responses = responses
        get() = if (field.isEmpty()) Response.exitResponse else field

    /**
     * If this conversation have been held, setter always sets this to `true`
     */
    @JsonIgnore
    var hasBeenRead: Boolean = false
        set(_) {
            field = true
        }

    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val RESPONSE_PATH = "responses"

        val endConversation = Conversation(
            "(Conversation ended)",
            "End of Conversation",
            Response.exitResponse
        )
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
