package no.uib.inf219.example.data

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import javafx.scene.control.Tooltip
import no.uib.inf219.api.serialization.Identifiable
import no.uib.inf219.api.serialization.storage.RetrievableStorage
import no.uib.inf219.api.serialization.storage.StoreHandler
import no.uib.inf219.example.data.prerequisite.Prerequisite

/**
 * @author Elg
 */
class Response : Identifiable<String> {

    @JsonPropertyDescription("How the user will response to the parent conversation text")
    @JsonProperty("response", required = true)
    var response: String = ""

    @JsonPropertyDescription("Name of this response for later referencing. Must be unique")
    @JsonProperty("name", required = false, defaultValue = "\"Response\"")
    var name: String = ""

    @JsonPropertyDescription("The following conversation this response lead to. If null the conversation will end")
    @JsonProperty("conv", required = false)
    var conv: Conversation? = null

    @JsonPropertyDescription("What prerequisites must be fulfilled in order for the user to choose this response")
    @JsonProperty("prerequisites", required = false)
    var prereq: Prerequisite? = null


    companion object {
        val exitResponse: MutableList<Response> = mutableListOf(create("End conversation", "Exit"))

        @JvmStatic
        var createId = 0

        @JvmStatic
        fun create(
            text: String,
            name: String? = null,
            conv: Conversation? = null,
            prereq: Prerequisite? = null
        ): Response {
            val resp = Response()
            resp.response = text
            if (name != null)
                resp.name = name
            if (conv != null)
                resp.conv = conv
            if (prereq != null)
                resp.prereq = prereq
            return resp
        }
    }

    init {
        val store: RetrievableStorage<String, Response> = StoreHandler.getStore(Response::class.java)
        store.store(this)
    }

    /**
     * @return If the conversation should close
     */
    fun shouldClose(): Boolean {
        return conv == null
    }

    fun tooltip(): Tooltip? {
        return if (shouldClose()) Tooltip("This will end the conversation") else null
    }

    override fun getId(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Response) return false

        if (response != other.response) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = response.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (conv?.hashCode() ?: 0)
        return result
    }
}
