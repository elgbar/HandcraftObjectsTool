package no.uib.inf219.gui.backend


import com.fasterxml.jackson.annotation.JsonIgnore
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
import no.uib.inf219.extra.removeNl
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.converter.UUIDStringConverter
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
 *
 * @author Elg
 */
abstract class SimpleClassBuilder<T : Any>(
    primClass: Class<T>,
    private val initialValue: T,
    override val name: String,
    override val parent: ClassBuilder<*>?,
    override val property: PropertyWriter?,
    val immutable: Boolean,
    @JsonIgnore
    val converter: StringConverter<T>
) : ReferencableClassBuilder<T>() {

    override val type: JavaType = ClassInformation.toJavaType(primClass)

    companion object {

        internal fun <E> findProperty(type: JavaType, initialValue: E): Property<E> {
            val p = when (type.rawClass) {
                Int::class.javaPrimitiveType -> intProperty(initialValue as Int)
                Long::class.javaPrimitiveType -> longProperty(initialValue as Long)
                Double::class.javaPrimitiveType -> doubleProperty(initialValue as Double)
                Float::class.javaPrimitiveType -> floatProperty(initialValue as Float)
                Boolean::class.javaPrimitiveType -> booleanProperty(initialValue as Boolean)
                String::class.java -> stringProperty(initialValue as String)

//              Short::class.javaPrimitiveType -> shortProperty(initialValue as Short)
//              Byte::class.javaPrimitiveType -> byteProperty(initialValue as Byte)
//              Char::class.javaPrimitiveType -> charProperty(initialValue as Boolean)
                else -> SimpleObjectProperty<E>(initialValue)
            }
            @Suppress("UNCHECKED_CAST")
            return p as Property<E>
        }
    }

    @get:JsonIgnore
    internal val valueProperty: Property<T> by lazy { findProperty(type, initialValue) }

    fun valueProperty(): ObservableValue<T> = valueProperty

    override var serializationObject: T
        get() = valueProperty.value
        set(value) = valueProperty.setValue(value)

    init {
        valueProperty.onChange {
            if (immutable) {
                throw IllegalStateException("Class builder ${this::class.simpleName} is immutable")
            }
            recompile()
        }
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
                recompile()
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

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<Any>? {
        return null
    }

    /**
     * Reset this simple class builder's value, as it has not properties we can safely ignore it
     */
    override fun reset(): Boolean {
        serializationObject = initialValue
        return false
    }

    override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?) {}

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>> = emptyMap()

    override fun isImmutable(): Boolean = immutable

    override fun getChildren(): List<ClassBuilder<*>> = emptyList()

    override fun isLeaf(): Boolean = true

    override fun getPreviewValue(): String = serializationObject.toString()

    override fun getChildType(cb: ClassBuilder<*>): JavaType? = null

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

    override fun toString(): String {
        return "Simple CB; value=$serializationObject, clazz=$type)"
    }

    @Suppress("DuplicatedCode")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SimpleClassBuilder<*>) return false

        if (name != other.name) return false
        if (serializationObject != other.serializationObject) return false
        if (parent != other.parent) return false
        if (immutable != other.immutable) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + serializationObject.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + immutable.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }


}
