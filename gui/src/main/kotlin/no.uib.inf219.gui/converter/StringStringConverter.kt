package no.uib.inf219.gui.converter

import javafx.util.StringConverter

/**
 * A kinda weird class. It converts strings to... strings.
 *
 * @author Elg
 */
object StringStringConverter : StringConverter<String>() {

    override fun toString(o: String?): String? {
        return o
    }

    override fun fromString(string: String?): String? {
        return string
    }

}
