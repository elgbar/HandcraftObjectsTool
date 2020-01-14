package no.uib.inf219.example.data

import no.uib.inf219.api.serialization.Serializable
import no.uib.inf219.api.serialization.storage.SerializableStorage
import no.uib.inf219.api.serialization.util.SerializationUtil
import no.uib.inf219.example.gui.Main
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * TODO allow for multiple pages of text
 *
 * @author Elg
 */
class Conversation(
    val text: String,
    val name: String = "Conversation #$convCount",
    val responses: List<Response> = listOf(Response.exitResponse),
    val uuid: UUID = UUID.randomUUID()
) : Serializable {

    init {
        Main.conversations.store(this)
    }

    /**
     * If this conversation have been held, setter always sets this to `true`
     */
    var hasBeenRead: Boolean = false
        set(_) {
            field = true
        }

    companion object {
        const val NAME_PATH = "name"
        const val TEXT_PATH = "text"
        const val RESPONSE_PATH = "responses"

        private var convCount = 0

        init {
            convCount++
        }

        //TODO replace this with Constrcut! (see UUIDConstruct)
        @JvmStatic
        fun deserialize(map: Map<String, Any>): Conversation {
            val uuidObj: Any = map[SerializableStorage.ID_PATH]
                ?: throw IllegalArgumentException("Given map does not contain any identification at ${SerializableStorage.ID_PATH} : map = $map")
            val uuid = SerializationUtil.toUUID(uuidObj)

            val text = map[TEXT_PATH] as String
            val name = map[NAME_PATH] as String? ?: "Conversation #${++convCount}"
            val responses = ArrayList<Response>()
            for (any in map[RESPONSE_PATH] as List<*>) {
                when {
                    any is Response -> {
                        responses += any
                    }
                    any != null -> {
                        throw IllegalArgumentException("One of the responses is not a response but a ${any::class.simpleName}")
                    }
                    else -> {
                        throw IllegalArgumentException("One of the responses is null")
                    }
                }
            }

            return Conversation(text, name, responses, uuid)
        }

        val endConversation = Conversation(
            "(Conversation ended)",
            "End of Conversation",
            listOf(Response("End conversation", end = true, conv = Conversation("")))
        )
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[TEXT_PATH] = text
        if (name.isNotEmpty())
            map[NAME_PATH] = name
        if (responses != listOf(Response.exitResponse))
            map[RESPONSE_PATH] = responses
        map[SerializableStorage.ID_PATH] = uuid
        return map
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
