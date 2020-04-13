package no.uib.inf219.extra

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

/**
 * Listen for changes to this observable. [test] if the listener should be removed, if [test] return `true` it will be removed. The test is done after the op
 * The lambda receives the changed value when the change occurs, which may be null,
 */
fun <T> ObservableValue<T>.onChangeUntil(test: () -> Boolean, op: (T?) -> Unit) {
    val listener = object : ChangeListener<T> {
        override fun changed(observable: ObservableValue<out T>?, oldValue: T, newValue: T) {
            op(newValue)
            if (test()) {
                removeListener(this)
            }
        }
    }
    addListener(listener)
}
