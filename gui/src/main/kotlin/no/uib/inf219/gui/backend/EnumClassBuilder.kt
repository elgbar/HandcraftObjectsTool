package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import tornadofx.combobox

/**
 * @author Elg
 */
class EnumClassBuilder<T : Enum<*>>(
    clazz: Class<T>,
    initialValue: T,
    name: String,
    parent: ClassBuilder<*>? = null,
    property: PropertyWriter? = null
) : SimpleClassBuilder<T>(
    clazz, initialValue, name, parent, property, false, EnumConverter(clazz)
) {

    private val enumValues: Array<T> = findEnumValues(clazz)

    init {
        require(clazz.isEnum) { "Given class is not an enum class" }
    }

    companion object {

        /**
         * When [enumValues<T>] cannot be used
         */
        fun <T> findEnumValues(enumClass: Class<T>): Array<T> {
            require(enumClass.isEnum) { "Given class is not an enum class" }
            return enumClass.getMethod("values").invoke(null) as Array<T>
        }
    }

    override fun editView(parent: Pane): Node {
        return parent.combobox<T>(
            property = serObjectProperty,
            values = enumValues.toList()
        )
    }

    internal class EnumConverter<T : Enum<*>>(clazz: Class<T>) : StringConverter<T>() {

        private val values = HashMap<String, T>()

        init {
            val enumValues: Array<T> = findEnumValues(clazz)
            for (enum in enumValues) {
                values[enum.name] = enum
            }
        }

        override fun toString(enum: T?): String? {
            return enum?.name
        }

        override fun fromString(name: String?): T? {
            return if (name == null) null else values[name]
        }
    }
}
