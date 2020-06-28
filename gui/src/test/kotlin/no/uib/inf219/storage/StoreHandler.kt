/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
