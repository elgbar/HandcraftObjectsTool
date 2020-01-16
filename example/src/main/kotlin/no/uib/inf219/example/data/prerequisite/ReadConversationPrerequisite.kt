package no.uib.inf219.example.data.prerequisite

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.example.data.Conversation

/**
 * @author Elg
 */
class ReadConversationPrerequisite(
    @JsonProperty("conversation", required = true)
    val conv: Conversation
) : Prerequisite {

    override fun check(): Boolean {
        return conv.hasBeenRead
    }

    override fun reason(): String {
        return "The given conversation '${conv.name}' has not been read"
    }
}
