package no.uib.inf219.extra

import javafx.beans.InvalidationListener
import javafx.beans.Observable

/**
 * @author Elg
 */
fun Observable.onChange(block: InvalidationListener.() -> Unit) {
    addListener(object : InvalidationListener {
        override fun invalidated(observable: Observable?) {
            this.block()
        }
    })
}
