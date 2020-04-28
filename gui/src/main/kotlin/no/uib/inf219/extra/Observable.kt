package no.uib.inf219.extra

import javafx.beans.InvalidationListener
import javafx.beans.Observable

/**
 * @author Elg
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
