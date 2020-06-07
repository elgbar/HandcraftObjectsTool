package no.uib.inf219.example.storage

/**
 * @author Elg
 */
open class IdentifiableStorage<I, R : Identifiable<I>> : RetrievableStorage<I, R> {

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
        store(store.getId(), store)
    }


    override fun store(id: I, store: R) {
        map[id] = store
    }

    override fun tryRetrieve(id: I): R? {
        return map[id]
    }

    override fun update(elem: R) {
        if (!map.containsValue(elem)) return
        for ((k, v) in map.entries) {
            if (v === elem) {
                map.remove(k)
                map[v.getId()] = v
                return
            }
        }
    }

}
