package no.uib.inf219.gui.backend.simple

import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.combobox
import tornadofx.onChange

/**
 * @author Elg
 */
class EnumClassBuilder<T : Enum<*>>(
    clazz: Class<T>,
    initialValue: T,
    name: ClassBuilder<*>? = null,
    parent: ClassBuilder<*>? = null,
    property: ClassInformation.PropertyMetadata? = null
) : SimpleClassBuilder<T>(
    clazz, initialValue, name, parent, property, false,
    EnumConverter(clazz)
) {

    private val enumValues = findEnumValues(clazz)

    override fun editView(parent: Pane): Node {
        return parent.combobox(
            property = serObjectObservable,
            values = enumValues
        ) {
            //Select the initial value as the first one
            value = initialValue

            selectionModel.selectedItemProperty().onChange {
                print("new $it")
            }

            setOnKeyPressed { event ->
                //find the first enum that starts with the given text and make it the selected value
                enumValues.find {
                    it.name.startsWith(event.text, true)
                }.also {
                    selectionModel.select(it)
                }
            }
        }
    }

    companion object {

        /**
         * When [enumValues<T>] cannot be used
         */
        fun <T : Enum<*>> findEnumValues(enumClass: Class<T>): List<T> {
            require(enumClass.isEnum) { "Given class is not an enum class" }
            @Suppress("UNCHECKED_CAST")
            return (enumClass.getMethod("values").invoke(null) as Array<T>).toList().sortedBy { it.name }
        }
    }

    internal class EnumConverter<T : Enum<*>>(clazz: Class<T>) : StringConverter<T>() {

        //key is nullable to allow for shorter fromString :)
        private val values: Map<String?, T> = findEnumValues(clazz).map { it.name to it }.toMap()

        override fun toString(enum: T?) = enum?.name

        override fun fromString(name: String?) = values[name]
    }
}
