package no.uib.inf219.extra

import javafx.beans.InvalidationListener
import javafx.beans.Observable

/**
 * A listener that will call the given block when it is invalidated.
 * The returned value can be used to remove the listener.
 *
 * @return The actual listener allow for removal of it.
 * @author Elg
 *
 * @see Observable.removeListener
 *
 */
fun Observable.onChange(block: InvalidationListener.() -> Unit): InvalidationListener {
    val listener = object : InvalidationListener {
        override fun invalidated(observable: Observable?) {
            this.block()
        }
    }
    addListener(listener)
    return listener
}
