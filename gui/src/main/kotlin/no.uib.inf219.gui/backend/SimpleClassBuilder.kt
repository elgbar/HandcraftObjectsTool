package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.util.StringConverter
import javafx.util.converter.*
import no.uib.inf219.gui.backend.SimpleClassBuilder.Companion.VALUE_KEY
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.ViewModel
import tornadofx.textarea
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

/**
 * A class builder intended for primitive classes to be used as leaf nodes in the class builder tree.
 * It only accept a single key called [VALUE_KEY] which will return the single value stored here.
 *
 * Every sub-class probably want to override
 *
 * @author Elg
 */
abstract class SimpleClassBuilder<T : Any> internal constructor(
    primClass: Class<T>,
    initialValue: T,
    private val converter: StringConverter<T>? = null
) :
    ClassBuilder<T> {

    override val clazz: JavaType = ClassInformation.toJavaType(primClass)

    private val valueProperty: SimpleObjectProperty<T> by lazy { SimpleObjectProperty<T>() }
    fun valueProperty(): ObservableValue<T> = valueProperty
    var value: T
        get() = valueProperty.get()
        set(value: T) = valueProperty.set(value)

    init {
        value = initialValue
    }

    companion object {
        const val VALUE_KEY = "value"
        val VALUE_KEY_SET = setOf(VALUE_KEY)
    }

    override fun getValidKeys(): Set<String> = VALUE_KEY_SET

    override fun toObject(): T? = value

    override fun isLeaf(): Boolean {
        return true
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>> {
        return mapOf()
    }

    override fun get(key: String): Any {
        return value
    }

    override fun set(key: String, value: Any) {
        require(key == VALUE_KEY) { "The key cannot be anything other than $VALUE_KEY" }
        require(clazz.isTypeOrSuperTypeOf(value.javaClass)) { "Given value is not equal to or a subtype of ${clazz.typeName}" }
        @Suppress("UNCHECKED_CAST") //checked above
        this.value = value as T
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleClassBuilder<*>) return false

        if (value != other.value) return false
        if (clazz != other.clazz) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + clazz.hashCode()
        return result
    }

    override fun toString(): String {
        return "SimpleClassBuilder(value=$value, clazz=$clazz)"
    }

    override fun toView(par: EventTarget): Node {
        return par.textarea {

            bindStringProperty(textProperty(), converter, valueProperty)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getDefaultConverter(): StringConverter<T>? = when (clazz.rawClass) {
        Int::class.javaPrimitiveType -> IntegerStringConverter()
        Long::class.javaPrimitiveType -> LongStringConverter()
        Double::class.javaPrimitiveType -> DoubleStringConverter()
        Float::class.javaPrimitiveType -> FloatStringConverter()
        Date::class -> DateStringConverter()
        BigDecimal::class -> BigDecimalStringConverter()
        BigInteger::class -> BigIntegerStringConverter()
        Number::class -> NumberStringConverter()
        LocalDate::class -> LocalDateStringConverter()
        LocalTime::class -> LocalTimeStringConverter()
        LocalDateTime::class -> LocalDateTimeStringConverter()
        Boolean::class.javaPrimitiveType -> BooleanStringConverter()
        else -> null
    } as StringConverter<T>?

    private fun bindStringProperty(
        stringProperty: StringProperty,
        converter: StringConverter<T>?,
        property: ObservableValue<T>
    ) {
        if (stringProperty.isBound) stringProperty.unbind()

        ViewModel.register(stringProperty, property)

        @Suppress("UNCHECKED_CAST")
        if (clazz.isTypeOrSuperTypeOf(String::class.java)) when {
            else -> stringProperty.bindBidirectional(property as Property<String>)
        } else {
            val effectiveConverter = converter ?: getDefaultConverter<T>()
            when {
                effectiveConverter != null -> stringProperty.bindBidirectional(
                    property as Property<T>,
                    effectiveConverter
                )
                else -> throw IllegalArgumentException("Cannot convert from $clazz to String without an explicit converter")
            }
        }
    }
}
