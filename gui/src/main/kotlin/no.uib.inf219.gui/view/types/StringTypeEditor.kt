package no.uib.inf219.gui.view.types

import javafx.beans.property.SimpleStringProperty
import tornadofx.bind
import tornadofx.textarea

/**
 * Simplest type editor, checks for everything
 *
 * @author Elg
 */
object StringTypeEditor : TypeEditor<String>() {

    private val value = SimpleStringProperty()

    override val root = textarea {
        bind(value)
    }

    /**
     * if current value is `null` this method returns an empty string
     */
    override fun toObject(): String {
        return value.valueSafe
    }

    /**
     * Strings can never be invalid
     */
    override fun validate(): Boolean {
        return true
    }

}
