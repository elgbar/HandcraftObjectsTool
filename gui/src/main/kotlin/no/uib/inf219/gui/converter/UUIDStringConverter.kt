package no.uib.inf219.gui.converter

import javafx.util.StringConverter
import java.util.*

/**
 *
 * Convert a string to and from [UUID] using [UUID.toString] and [UUID.fromString] respectivly
 *
 * @author Elg
 */
object UUIDStringConverter : StringConverter<UUID>() {

    /**
     * @return The given `uuid` as String, if input is `null` the returned value is also `null`
     *
     * @see UUID.toString
     */
    override fun toString(uuid: UUID?): String? {
        return uuid?.toString()
    }

    /**
     *
     * @return The given string as UUID if it is valid, if input is `null` the returned value is also `null`
     *
     * @throws IllegalArgumentException see [UUID.fromString]
     * @see UUID.fromString
     */
    override fun fromString(string: String?): UUID? {
        return if (string != null) UUID.fromString(string) else null
    }
}
