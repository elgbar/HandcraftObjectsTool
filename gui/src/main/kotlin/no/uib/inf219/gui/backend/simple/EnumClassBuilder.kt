package no.uib.inf219.gui.backend.simple

import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import tornadofx.combobox

/**
 * @author Elg
 */
class EnumClassBuilder<T : Enum<*>>(
    clazz: Class<T>,
    initialValue: T,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    property: PropertyWriter? = null
) : SimpleClassBuilder<T>(
    clazz, initialValue, name, parent, property, false,
    EnumConverter(clazz)
) {

    private val enumValues = findEnumValues(clazz)

    init {
        require(clazz.isEnum) { "Given class is not an enum class" }
    }

    companion object {

        /**
         * When [enumValues<T>] cannot be used
         */
        fun <T : Enum<*>> findEnumValues(enumClass: Class<T>): List<T> {
            require(enumClass.isEnum) { "Given class is not an enum class" }
            return (enumClass.getMethod("values").invoke(null) as Array<T>).toList().sortedBy { it.name }
        }
    }

    override fun editView(parent: Pane): Node {
        return parent.combobox(
            property = serObjectObservable,
            values = enumValues
        ) {
            //Select the initial value as the first one
            value = initialValue

            setOnKeyPressed { event ->
                //find the first enum that starts with the given text and make it the selected value
                enumValues.find { it.name.startsWith(event.text, true) }.also {
                    if (it != null) {
                        value = it
                    }
                }
            }
        }
    }

    internal class EnumConverter<T : Enum<*>>(clazz: Class<T>) : StringConverter<T>() {

        private val values = findEnumValues(clazz).map { it.name to it }.toMap()

        override fun toString(enum: T?): String? {
            return enum?.name
        }

        override fun fromString(name: String?): T? {
            return if (name == null) null else values[name]
        }
    }
}
