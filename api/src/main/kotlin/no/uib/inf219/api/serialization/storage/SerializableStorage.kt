package no.uib.inf219.api.serialization.storage

import no.uib.inf219.api.serialization.Serializable

/**
 * @author Elg
 */
open class SerializableStorage<I, R : Serializable>(override val clazz: Class<I>) : RetrievableStorage<I, R> {

    companion object {
        const val ID_PATH = "id"
    }

    private val map: MutableMap<I, R> = HashMap()

    /**
     * Convert the id object to [I]
     *
     * @return the object as [I] or `null` if an error occurred (ie classCastException)
     */
    open fun toSerObj(obj: Any): I {
        @Suppress("UNCHECKED_CAST")
        return obj as I
    }

    override fun store(store: R) {
        val ser: Map<String, Any?> = store.serialize()

        val idObj: Any = ser[ID_PATH]
            ?: throw IllegalArgumentException("Failed to find an object at $ID_PATH when serializing given object '$store'")

        val id: I
        try {
            id = toSerObj(idObj)
        } catch (e: Exception) {
            throw IllegalArgumentException("Given object to store does not have the id object as ${clazz.name}", e)
        }
        store(id, store)
    }


    override fun store(id: I, store: R) {
        map[id] = store
    }

    override fun tryRetrieve(id: I): R? {
        return map[id]
    }

}
