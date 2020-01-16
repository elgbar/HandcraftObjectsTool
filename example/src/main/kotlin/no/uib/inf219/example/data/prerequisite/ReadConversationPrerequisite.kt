package no.uib.inf219.example.data.prerequisite

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.example.data.Conversation

/**
 * @author Elg
 */
class ReadConversationPrerequisite : Prerequisite {

    @JsonProperty("conversation", required = true)
    lateinit var conv: Conversation

    override fun check(): Boolean {
        return conv.hasBeenRead
    }

    override fun reason(): String {
        return "The given conversation '${conv.name}' has not been read"
    }
}
