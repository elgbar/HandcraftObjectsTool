package no.uib.inf219.gui.backend.simple

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import javafx.collections.transformation.FilteredList
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.SelectionMode
import javafx.scene.control.TreeItem
import javafx.util.StringConverter
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.SimpleClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.*
import java.lang.reflect.Field

/**
 * @author Elg
 */
class EnumClassBuilder<T : Enum<*>>(
    clazz: Class<T>,
    initialValue: T? = null,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    property: ClassInformation.PropertyMetadata? = null,
    item: TreeItem<ClassBuilderNode>
) : SimpleClassBuilder<T>(
    clazz.kotlin,
    initialValue ?: getDefaultEnumValue(clazz),
    key,
    parent,
    property,
    false,
    EnumConverter(clazz),
    item
) {

    private val enumValues = findEnumValues(clazz).asObservable()
    private val filteredValues = FilteredList(enumValues)

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.vbox {
            addClass(Styles.parent)
            textfield {
                promptText = "Enum name"
                textProperty().onChange {
                    if (text.isNullOrBlank()) {
                        filteredValues.predicate = null
                    } else {
                        filteredValues.setPredicate {
                            it.name.contains(text, ignoreCase = true)
                        }
                    }
                }
            }

            listview(filteredValues) {
                selectionModel.selectionMode = SelectionMode.SINGLE
                selectionModel.select(initialValue)

                filteredValues.onChange {
                    if (filteredValues.contains(serObject)) {
                        selectionModel.select(serObject)
                    }
                }
                onUserSelect(1) {
                    serObject = it
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
            return enumClass.enumConstants.sortedBy { it.name }
        }

        fun <T : Enum<*>> getDefaultEnumValue(enumClass: Class<T>): T {
            val enumConstants = findEnumValues(enumClass)
            for (value in enumConstants) {
                val field: Field = enumClass.getField(value.name)
                if (field.isAnnotationPresent(JsonEnumDefaultValue::class.java)) {
                    return value
                }
            }
            return enumConstants[0]
        }
    }

    internal class EnumConverter<T : Enum<*>>(clazz: Class<T>) : StringConverter<T>() {

        //key is nullable to allow for shorter fromString :)
        private val values: Map<String?, T> = findEnumValues(clazz).map { it.name to it }.toMap()

        override fun toString(enum: T?) = enum?.name

        override fun fromString(name: String?) = values[name]
    }
}
