package no.uib.inf219.gui.view.types

import tornadofx.View

/**
 * @author Elg
 */
abstract class TypeEditor<T> : View() {

    /**
     * @return if valid or not
     */
    abstract fun validate(): Boolean

    /**
     * Convert the current data to the type we're creating
     */
    abstract fun toObject(): T

}
