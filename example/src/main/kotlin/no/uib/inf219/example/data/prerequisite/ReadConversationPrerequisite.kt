package no.uib.inf219.example.data.prerequisite

import no.uib.inf219.example.data.Conversation

/**
 * @author Elg
 */
class ReadConversationPrerequisite : Prerequisite {

    lateinit var conv: Conversation

    override fun check(): Boolean {
        return conv.hasBeenRead
    }

    override fun reason(): String {
        return "The given conversation '${conv.name}' has not been read"
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        if (!::conv.isInitialized) throw IllegalStateException("Cannot serialize an object that is not initialized: ${::conv.name} is lateinit but initialized")
        map[CONV_PATH] = conv
        return map
    }

    companion object {
        const val CONV_PATH = "conversation"

        @Suppress("DuplicatedCode")
        @JvmStatic
        fun deserialize(map: Map<String, Any?>): ReadConversationPrerequisite {
            val conv = map[CONV_PATH] as Conversation

            val pre = ReadConversationPrerequisite()
            pre.conv = conv
            return pre
        }
    }
}
