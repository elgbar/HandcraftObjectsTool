package no.uib.inf219.api.serialization.storage

import no.uib.inf219.api.serialization.Identifiable

/**
 * @author Elg
 */
object StoreHandler {

    private val stores: MutableMap<Class<*>, RetrievableStorage<*, *>> = HashMap()

    @JvmStatic
    fun getStore(clazz: Class<*>): RetrievableStorage<*, *> {
        tryCreateStore(clazz)
        return stores[clazz] ?: throw IllegalArgumentException("Failed")
    }

    private fun tryCreateStore(clazz: Class<out Any>) {
        if (stores.containsKey(clazz)) return
        if (Identifiable::class.java.isAssignableFrom(clazz)) {
            this.stores[clazz] = IdentifiableStorage(clazz)
        }
    }
}
