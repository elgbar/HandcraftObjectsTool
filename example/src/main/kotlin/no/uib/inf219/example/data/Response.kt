package no.uib.inf219.example.data

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javafx.scene.control.Tooltip

/**
 * @author Elg
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class Response(
    @JsonProperty("text", required = true)
    val text: String,

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("conv")
    val conv: Conversation? = null
) {

    companion object {
        val exitResponse = listOf(Response("End conversation", "Exit"))
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Response) return false

        if (text != other.text) return false
        if (name != other.name) return false
        if (conv != other.conv) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (conv?.hashCode() ?: 0)
        return result
    }


}
