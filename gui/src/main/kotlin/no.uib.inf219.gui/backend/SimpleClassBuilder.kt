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
import no.uib.inf219.gui.converter.UUIDStringConverter
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
 *
 * Every sub-class probably want to override
 *
 * @author Elg
 */
abstract class SimpleClassBuilder<T : Any> internal constructor(
    primClass: Class<T>,
    private val initialValue: T,
    override val parent: ClassBuilder<Any>? = null,
    private val converter: StringConverter<T>? = null
) :
    ClassBuilder<T> {

    override val javaType: JavaType = ClassInformation.toJavaType(primClass)

    private val valueProperty: SimpleObjectProperty<T> by lazy { SimpleObjectProperty<T>() }
    fun valueProperty(): ObservableValue<T> = valueProperty
    var value: T
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

    init {
        value = initialValue
    }

    override fun toObject(): T = value

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>> {
        return emptyMap()
    }

    override fun toString(): String {
        return "SimpleClassBuilder(value=$value, clazz=$javaType)"
    }

    override fun toView(par: EventTarget): Node {
        return par.textarea {
            bindStringProperty(textProperty(), converter, valueProperty)
        }
    }

    override fun createClassBuilderFor(name: String): ClassBuilder<Any>? {
        return null
    }

    /**
     * Reset the value this holds to the [initialValue] provided in the constructor
     */
    override fun reset(name: String): Boolean {
        value = initialValue
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleClassBuilder<*>) return false

        if (value != other.value) return false
        if (javaType != other.javaType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + javaType.hashCode()
        return result
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getDefaultConverter(): StringConverter<T>? = when (javaType.rawClass) {
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
        //non-default converts
        UUID::class -> UUIDStringConverter
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
        if (javaType.isTypeOrSuperTypeOf(String::class.java)) when {
            else -> stringProperty.bindBidirectional(property as Property<String>)
        } else {
            val effectiveConverter = converter ?: getDefaultConverter<T>()
            when {
                effectiveConverter != null -> stringProperty.bindBidirectional(
                    property as Property<T>,
                    effectiveConverter
                )
                else -> throw IllegalArgumentException("Cannot convert from $javaType to String without an explicit converter")
            }
        }
    }
}
