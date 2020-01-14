package no.uib.inf219.api.serialization.storage

import no.uib.inf219.api.serialization.Serializable
import no.uib.inf219.api.serialization.util.SerializationUtil
import java.util.*

/**
 * Allow for easy serialization of UUID objects, supports loading UUID from object (ie its already an UUID), string and byte array
 *
 * @author Elg
 */
class UUIDSerializableStorage<R : Serializable> : SerializableStorage<UUID, R>(UUID::class.java) {

    override fun toSerObj(obj: Any): UUID {
        return SerializationUtil.toUUID(obj)
    }
}
