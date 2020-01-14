package no.uib.inf219.api.serialization.storage

import no.uib.inf219.api.serialization.Serializable
import no.uib.inf219.api.serialization.storage.SerializableStorage.Companion.ID_PATH

/**
 *
 * [I] how to identify the object to retrive (ie a [R])
 *
 * @author Elg
 */
interface RetrievableStorage<I, R : Serializable> {

    val clazz: Class<I>

    /**
     * Store parameter [store] using the id found at [ID_PATH] when it is serialized
     *
     * @throws IllegalArgumentException if no object is found at [ID_PATH]
     * @throws IllegalArgumentException if object found at [ID_PATH] is not of type [I]
     */
    fun store(store: R)

    /**
     * Store the object [store] with the retrial code [id]. If there is already an object with the given identification the stored object will be overwritten
     *
     */
    fun store(id: I, store: R)

    /**
     * Retrieve the object with id [id]
     *
     * @return the object stored with identification [id], if nothing is found `null` is returned
     *
     * @see retrieve for a non-nullable approach
     */
    fun tryRetrieve(id: I): R?

    /**
     * Retrieve the object with id [id]
     *
     * @return the object stored with identification [id], if nothing is found an exception is thrown
     *
     * @throws IllegalArgumentException if there are no objects with the given identification
     * @see tryRetrieve for a nullable approach
     */
    fun retrieve(id: I): R {
        return tryRetrieve(id) ?: throw IllegalArgumentException("Failed to find a stored element with id '$id'")
    }

    /**
     * @return If there is stored an item with identification [id]
     */
    fun isStored(id: I): Boolean {
        return tryRetrieve(id) == null
    }

}
