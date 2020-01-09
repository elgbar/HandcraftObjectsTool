package no.uib.inf219.api.serialization

/**
 * Represents an object that may be serialized and deserialized.
 *
 *
 */
interface Serializer {

    /**
     * Creates a Map representation of this class.
     *
     * @return Map containing the current state of this class
     */
    fun serialize(): Map<String, Any?>

//    fun deserialize(args: Map<String, Any?>): Any {
//        throw NotImplementedError("This method must be overwritten!")
//    }
}
