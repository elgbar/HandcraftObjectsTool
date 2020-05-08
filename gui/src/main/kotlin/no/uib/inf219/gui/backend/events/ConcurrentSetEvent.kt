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
