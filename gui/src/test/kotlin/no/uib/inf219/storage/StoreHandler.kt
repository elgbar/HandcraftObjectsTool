package no.uib.inf219.example.storage

/**
 * @author Elg
 */
object StoreHandler {

    private val stores: MutableMap<Class<*>, RetrievableStorage<*, *>> = HashMap()
    private val impls: MutableMap<Class<*>, (clazz: Class<*>) -> RetrievableStorage<*, *>> = HashMap()

    init {
        addStoreType<Any, Identifiable<Any>>(Identifiable::class.java) {
            return@addStoreType IdentifiableStorage<Any, Identifiable<Any>>()
        }
    }

    /**
     * Add a store type to allow
     */
    fun <I, R> addStoreType(
        clazz: Class<out Any>,
        rsCreator: (clazz: Class<out R>) -> RetrievableStorage<I, R>
    ) {
        @kotlin.Suppress("UNCHECKED_CAST")
        impls[clazz] = rsCreator as (clazz: Class<*>) -> RetrievableStorage<*, *>
    }

    @JvmStatic
    fun <I, R> getStore(clazz: Class<R>): RetrievableStorage<I, R> {
        tryCreateStore(clazz)
        @kotlin.Suppress("UNCHECKED_CAST")
        return stores[clazz] as RetrievableStorage<I, R>? ?: throw NotImplementedError("Failed")
    }

    private fun <R> tryCreateStore(clazz: Class<R>) {
        if (stores.containsKey(clazz)) return
        for (impl in impls) {
            if (impl.key.isAssignableFrom(clazz)) {
                this.stores[clazz] = impl.value(clazz)
                return
            }
        }
        throw NotImplementedError("Failed to find an implementation of RetrievableStorage to use for class ${clazz.name}")
    }
}
