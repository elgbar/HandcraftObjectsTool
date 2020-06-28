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

import no.uib.inf219.example.storage.IdentifiableStorage.Companion.ID_PATH

/**
 *
 * [I] how to identify the object to retrieve (ie a [R])
 *
 * @author Elg
 */
interface RetrievableStorage<I, R> {

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

    fun update(elem: R)

}
