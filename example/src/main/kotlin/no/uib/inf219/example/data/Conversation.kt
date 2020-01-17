package no.uib.inf219.example.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.api.serialization.Identifiable
import no.uib.inf219.api.serialization.storage.RetrievableStorage
import no.uib.inf219.api.serialization.storage.StoreHandler

/**
 * TODO allow for multiple pages of text
 *
 * @author Elg
 */
class Conversation : Identifiable<String> {

    @JsonProperty("text", required = false)
    lateinit var text: String

    @JsonProperty("name", defaultValue = "Conversation")
    var name: String = "Conversation #${++createId}"
        set(value) {
            field = value
            val store: RetrievableStorage<String, Conversation> =
                StoreHandler.getStore(Conversation::class.java)
            store.update(this)
        }


    @JsonProperty("responses", defaultValue = "[]")
    var responses: MutableList<Response> = ArrayList()
        get() = if (field.isEmpty()) Response.exitResponse else field

    init {
//        println("storing name = ${name} ($text)")
        val store: RetrievableStorage<String, Conversation> =
            StoreHandler.getStore(Conversation::class.java)
        store.store(this)

    }

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
        val endConversation: Conversation = create("(Conversation ended)", "End of Conversation", Response.exitResponse)

        @JvmStatic
        fun create(text: String, name: String? = null, responses: MutableList<Response>? = null): Conversation {
            val conv = Conversation()
            conv.text = text

            if (name != null)
                conv.name = name
            if (responses != null)
                conv.responses = responses
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
        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + responses.hashCode()
        return result
    }


}
