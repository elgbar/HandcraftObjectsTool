package no.uib.inf219.extra

import javafx.beans.Observable
import javafx.beans.value.ObservableValue
import javafx.collections.*

/**
 * @author Elg
 */
fun Observable.onChange(block: () -> Unit) {
    when (this) {
        is ObservableValue<*> -> apply { addListener { o, oldValue, newValue -> block() } }
        is ObservableList<*> -> apply { addListener(ListChangeListener { block() }) }
        is ObservableSet<*> -> apply { addListener(SetChangeListener { block() }) }
        is ObservableArray<*> -> apply { addListener { _, _, _, _ -> block() } }
        is ObservableMap<*, *> -> apply { addListener(MapChangeListener { block() }) }
        else -> error("Cannot listen to changes for ${this::class}")
    }
}
