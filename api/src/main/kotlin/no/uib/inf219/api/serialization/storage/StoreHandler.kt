package no.uib.inf219.api.serialization.storage

import no.uib.inf219.api.serialization.Identifiable

/**
 * @author Elg
 */
object StoreHandler {

    private val stores: MutableMap<Class<*>, RetrievableStorage<*, *>> = HashMap()

    @JvmStatic
    fun <I, R> getStore(clazz: Class<R>): RetrievableStorage<I, R> {
        tryCreateStore(clazz)
        return stores[clazz] as RetrievableStorage<I, R>? ?: throw IllegalArgumentException("Failed")
    }

    private fun <R> tryCreateStore(clazz: Class<R>) {
        if (stores.containsKey(clazz)) return
        if (Identifiable::class.java.isAssignableFrom(clazz)) {
            this.stores[clazz] = IdentifiableStorage(clazz)
        }
    }
}
