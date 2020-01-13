package no.uib.inf219.example.data

import javafx.scene.control.Tooltip
import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * @author Elg
 */
class Response(
    val text: String,
    val name: String = "",
    val conv: Conversation = Conversation.endConversation,
    val end: Boolean = false
) : ConfigurationSerializable {

    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val SUB_CONVERSATION_PATH = "conversations"
        const val END_PATH = "end"

        @Suppress("unused")
        @JvmStatic
        fun deserialize(map: Map<String, Any?>): Response {
            val text = map[TEXT_PATH] as String
            val name = map[NAME_PATH] as String? ?: ""
            val conv = map[SUB_CONVERSATION_PATH] as Conversation? ?: Conversation.endConversation
            val end = map[END_PATH] as Boolean? ?: false
            return Response(text, name, conv, end)
        }

        val exitResponse = Response("End conversation", "Exit", Conversation("", ""), true)
    }

    /**
     * @return If the conversation should close
     */
    fun shouldClose(): Boolean {
        return end
    }

    fun tooltip(): Tooltip? {
        return if (end) Tooltip("This will end the conversation") else null
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[TEXT_PATH] = text
        if (name.isNotEmpty())
            map[NAME_PATH] = name
        if (conv != Conversation.endConversation)
            map[SUB_CONVERSATION_PATH] = conv
        if (end)
            map[END_PATH] = true
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Response) return false

        if (text != other.text) return false
        if (name != other.name) return false
        if (end != other.end) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + end.hashCode()
        return result
    }


}
