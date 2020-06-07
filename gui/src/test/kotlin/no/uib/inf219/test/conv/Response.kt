package no.uib.inf219.test.conv

import com.fasterxml.jackson.annotation.JsonProperty
import javafx.scene.control.Tooltip
import no.uib.inf219.example.storage.Identifiable
import no.uib.inf219.example.storage.RetrievableStorage
import no.uib.inf219.example.storage.StoreHandler

/**
 * @author Elg
 */
class Response : Identifiable<String> {

    @JsonProperty("response", required = true)
    var response: String = "???"

    @JsonProperty("name", required = false, defaultValue = "Response")
    var name: String = "Response #${++createId}"

    @JsonProperty("conv", required = false)
    var conv: Conversation? = null

    companion object {
        val exitResponse: List<Response> = listOf(
            create("End conversation", "Exit")
        )

        @JvmStatic
        var createId = 0

        @JvmStatic
        fun create(
            text: String,
            name: String? = null,
            conv: Conversation? = null
        ): Response {
            val resp = Response()
            resp.response = text
            if (name != null)
                resp.name = name
            if (conv != null)
                resp.conv = conv
            return resp
        }
    }

    init {
        val store: RetrievableStorage<String, Response> = StoreHandler.getStore(
            Response::class.java
        )
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
        return conv === other.conv
    }

    override fun hashCode(): Int {
        var result = response.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "Response(response='$response', name='$name', conv=$conv)"
    }

}
