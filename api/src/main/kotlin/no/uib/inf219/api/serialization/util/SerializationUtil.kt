package no.uib.inf219.api.serialization.util

import java.util.*

/**
 * @author Elg
 */
object SerializationUtil {

    /**
     * Convert an object to [UUID]
     *
     * @return The object as [UUID]
     *
     * @throws IllegalArgumentException If the object cannot be converted to [UUID]
     */
    fun toUUID(obj: Any): UUID {
        return try {
            obj as UUID
        } catch (ignore: ClassCastException) {
            try {
                UUID.fromString(obj as String)
            } catch (ignore: ClassCastException) {
                try {
                    UUID.nameUUIDFromBytes(obj as ByteArray)
                } catch (ignore: ClassCastException) {
                    throw IllegalArgumentException("Failed to convert given object to UUID: obj = '$obj'")
                }
            }
        }
    }

}
