package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.layout.Pane
import javafx.util.StringConverter
import javafx.util.converter.*
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.converter.UUIDStringConverter
import no.uib.inf219.gui.extra.removeNl
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.OutputArea
import tornadofx.*
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * A class builder intended for primitive classes to be used as leaf nodes in the class builder tree.
 *
 * Every sub-class probably want to override
 *
 * @author Elg
 */
abstract class SimpleClassBuilder<T : Any>(
    primClass: Class<T>,
    private val initialValue: T,
    override val parent: ClassBuilder<*>? = null,
    override val name: String? = null,
    override val property: PropertyWriter? = null,
    val converter: StringConverter<T>
) : ClassBuilder<T> {

    override val type: JavaType = ClassInformation.toJavaType(primClass)

    val valueProperty: Property<T> by lazy {
        val p = when (type.rawClass) {
            Int::class.javaPrimitiveType -> intProperty(initialValue as Int)
//            Short::class.javaPrimitiveType -> shortProperty(initialValue as Short)
//            Byte::class.javaPrimitiveType -> byteProperty(initialValue as Byte)
            Long::class.javaPrimitiveType -> longProperty(initialValue as Long)
            Double::class.javaPrimitiveType -> doubleProperty(initialValue as Double)
            Float::class.javaPrimitiveType -> floatProperty(initialValue as Float)
            Boolean::class.javaPrimitiveType -> booleanProperty(initialValue as Boolean)
            String::class.javaPrimitiveType -> stringProperty(initialValue as String)
//            Char::class.javaPrimitiveType -> charProperty(initialValue as Boolean)
            else -> SimpleObjectProperty<T>()
        }
        @Suppress("UNCHECKED_CAST")
        return@lazy p as Property<T>
    }

    fun valueProperty(): ObservableValue<T> = valueProperty
    var value: T
        get() = valueProperty.value
        set(value) = valueProperty.setValue(value)

    init {
        valueProperty.value = initialValue
    }

    override fun toObject(): T = value

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>> {
        return emptyMap()
    }

    override fun toView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.vbox {
            addClass(Styles.parent)
            vbox {
                addClass(Styles.parent)
                label("Required? ${isRequired()}")
                label("Type: ${type.rawClass}")
            }
            this += editView(this)
        }
    }

    /**
     * How to view the edit the value
     */
    open fun editView(parent: Pane): Node {
        return parent.textfield {
            textFormatter = TextFormatter<T>() {

                val text = it.controlNewText.removeNl().trim()

                if (it.isContentChange && text.isNotEmpty() && !validate(text)) {
                    OutputArea.logln { "Failed to parse '$text' to ${this@SimpleClassBuilder.initialValue::class.simpleName}" }
                    return@TextFormatter null
                }
                return@TextFormatter it
            }
            bindStringProperty(textProperty(), converter, valueProperty)
        }
    }

    /**
     * Validate if a given string is a correctly formatted.
     * An empty string will never be given
     */
    open fun validate(text: String): Boolean {
        return try {
            converter.fromString(text) != null
        } catch (e: Throwable) {
            false
        }
    }

    override fun createClassBuilderFor(property: String): ClassBuilder<Any>? {
        return null
    }

    /**
     * Reset this simple class builder's value, as it has not properties we can safely ignore it
     */
    fun reset(): ClassBuilder<*>? {
        value = initialValue
        return this
    }

    /**
     *
     * Reset the value this holds to the [initialValue] provided in the constructor
     */
    override fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>? {
        require(element == null || element == this) { "Given element is not null or this" }
        return reset()
    }

    override fun isLeaf(): Boolean {
        return true
    }

    override fun previewValue(): String {
        return value.toString()
    }


    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getDefaultConverter(): StringConverter<T>? = when (type.rawClass) {
        Int::class.javaPrimitiveType -> IntegerStringConverter()
        Long::class.javaPrimitiveType -> LongStringConverter()
        Double::class.javaPrimitiveType -> DoubleStringConverter()
        Float::class.javaPrimitiveType -> FloatStringConverter()
        Boolean::class.javaPrimitiveType -> BooleanStringConverter()
        Short::class.javaPrimitiveType -> ShortStringConverter()
        Byte::class.javaPrimitiveType -> ByteStringConverter()
        Char::class.javaPrimitiveType -> CharacterStringConverter()
        Date::class -> DateStringConverter()
        BigDecimal::class -> BigDecimalStringConverter()
        BigInteger::class -> BigIntegerStringConverter()
        Number::class -> NumberStringConverter()
        LocalDate::class -> LocalDateStringConverter()
        LocalTime::class -> LocalTimeStringConverter()
        LocalDateTime::class -> LocalDateTimeStringConverter()
        //non-default converts
        UUID::class -> UUIDStringConverter
        else -> null
    } as StringConverter<T>?

    fun bindStringProperty(
        stringProperty: StringProperty,
        converter: StringConverter<T>?,
        property: ObservableValue<T>
    ) {
        if (stringProperty.isBound) stringProperty.unbind()

        ViewModel.register(stringProperty, property)

        @Suppress("UNCHECKED_CAST")
        if (type.isTypeOrSuperTypeOf(String::class.java)) when {
            else -> stringProperty.bindBidirectional(property as Property<String>)
        } else {
            val effectiveConverter = converter ?: getDefaultConverter<T>()
            when {
                effectiveConverter != null -> stringProperty.bindBidirectional(
                    property as Property<T>,
                    effectiveConverter
                )
                else -> throw IllegalArgumentException("Cannot convert from $type to String without an explicit converter")
            }
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleClassBuilder<*>) return false

        if (value != other.value) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


    override fun toString(): String {
        return "Simple CB; value=$value, clazz=$type)"
    }
}
