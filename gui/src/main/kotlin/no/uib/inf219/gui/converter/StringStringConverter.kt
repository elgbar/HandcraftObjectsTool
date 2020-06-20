package no.uib.inf219.gui.converter

import javafx.util.StringConverter

/**
 * A kinda weird class. It converts strings to... strings.
 *
 * @author Elg
 */
object StringStringConverter : StringConverter<String>() {
    override fun toString(o: String?) = o
    override fun fromString(string: String?) = string
}
