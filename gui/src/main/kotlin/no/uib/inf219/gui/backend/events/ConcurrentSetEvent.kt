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

package no.uib.inf219.gui.backend.events

import kotlinx.event.AbstractEvent
import java.util.concurrent.ConcurrentHashMap

open class ConcurrentSetEvent<T>(val backing: MutableSet<(T) -> Unit> = ConcurrentHashMap.newKeySet()) :
    AbstractEvent<T>(), MutableSet<(T) -> Unit> {

    companion object {
        val LOCK = Any()
    }

    override fun add(element: (T) -> Unit): Boolean {
        synchronized(LOCK) {
            return backing.add(element)
        }
    }

    override fun addAll(elements: Collection<(T) -> Unit>): Boolean {
        synchronized(LOCK) {
            return backing.addAll(elements)
        }
    }

    override fun clear() {
        synchronized(LOCK) {
            backing.clear()
        }
    }

    override fun iterator(): MutableIterator<(T) -> Unit> {
        synchronized(LOCK) {
            return backing.iterator()
        }
    }

    override fun remove(element: (T) -> Unit): Boolean {
        synchronized(LOCK) {
            return backing.remove(element)
        }
    }

    override fun removeAll(elements: Collection<(T) -> Unit>): Boolean {
        synchronized(LOCK) {
            return backing.removeAll(elements)
        }
    }

    override fun retainAll(elements: Collection<(T) -> Unit>): Boolean {
        synchronized(LOCK) {
            return backing.retainAll(elements)
        }
    }

    override val size: Int = synchronized(LOCK) { backing.size }


    override fun contains(element: (T) -> Unit): Boolean {
        synchronized(LOCK) {
            return backing.contains(element)
        }
    }

    override fun containsAll(elements: Collection<(T) -> Unit>): Boolean {
        synchronized(LOCK) {
            return backing.containsAll(elements)
        }
    }

    override fun isEmpty(): Boolean {
        synchronized(LOCK) {
            return backing.isEmpty()
        }
    }
}
