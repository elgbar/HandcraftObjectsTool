@file:Suppress("LeakingThis")

package no.uib.inf219.gui.backend.cb.api


import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.Property
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TextFormatter
import javafx.scene.control.TreeItem
import javafx.util.StringConverter
import javafx.util.converter.*
import no.uib.inf219.extra.removeNl
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
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
import kotlin.reflect.KClass

/**
 * A class builder intended for primitive types and simple classes to be used as leaf nodes in the class builder tree.
 *
 * [key] and [parent] are nullable in constructor as it is needed to create more complex types and typically they are not used for actual object creation
 *
 * @author Elg
 */
abstract class SimpleClassBuilder<T : Any> constructor(
    primClass: KClass<T>,
    internal val initialValue: T,
    key: ClassBuilder?,
    parent: ParentClassBuilder?,
    override val property: ClassInformation.PropertyMetadata?,
    val immutable: Boolean,
    @JsonIgnore
    val converter: StringConverter<T>,
    override val item: TreeItem<ClassBuilderNode>
) : ClassBuilder {

    override val key: ClassBuilder = key ?: this
    override val parent: ParentClassBuilder = parent ?: FAKE_ROOT

    override val type: JavaType = primClass.type()

    init {
        require(key !== this) { "Cannot use a self reference as key!" }
    }

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
//              Char::class.javaPrimitiveType -> charProperty(initialValue as Char)
                else -> SimpleObjectProperty(initialValue)
            }
            @Suppress("UNCHECKED_CAST")
            return p as Property<E>
        }
    }

    @get:JsonIgnore
    final override val serObjectObservable: Property<T> by lazy {
        findProperty(
            type,
            initialValue
        )
    }

    override var serObject: T by serObjectObservable

    init {
        serObjectObservable.onChange {
            if (immutable) {
                throw IllegalStateException("Class builder ${this::class.simpleName} is immutable")
            }
        }
    }

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.textfield {
            textFormatter = TextFormatter<T>() {

                val text = it.controlNewText.removeNl().trim()

                if (it.isContentChange && text.isNotEmpty() && !validate(text)) {
                    OutputArea.logln { "Failed to parse '$text' to ${this@SimpleClassBuilder.serObject::class.simpleName}" }
                    return@TextFormatter null
                }
                return@TextFormatter it
            }
            bindStringProperty(textProperty(), converter, serObjectObservable)
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

    override fun isImmutable(): Boolean = immutable

    final override fun isLeaf(): Boolean = true

    override fun getPreviewValue(): String = serObject.toString()

    @Suppress("UNCHECKED_CAST")

    private fun <T : Any> getDefaultConverter(): StringConverter<T>? = when (type.rawClass.kotlin) {
        Int::class -> IntegerStringConverter()
        Long::class -> LongStringConverter()
        Double::class -> DoubleStringConverter()
        Float::class -> FloatStringConverter()
        Boolean::class -> BooleanStringConverter()
        Short::class -> ShortStringConverter()
        Byte::class -> ByteStringConverter()
        Char::class -> CharacterStringConverter()
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
        return "Simple CB; value=$serObject, clazz=$type)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimpleClassBuilder<*>

        if (initialValue != other.initialValue) return false
        if (property != other.property) return false
        if (immutable != other.immutable) return false
        if (converter != other.converter) return false
        if (type != other.type) return false
        if (serObject != other.serObject) return false

        if (key.serObject != other.key.serObject) return false

        //parent can be a self reference
        if (parent !== other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = initialValue.hashCode()
        result = 31 * result + (property?.hashCode() ?: 0)
        result = 31 * result + immutable.hashCode()
        result = 31 * result + converter.hashCode()
        result = 31 * result + type.hashCode()

        //parent can be a self reference
        result = 31 * result + (if (key !== this) key.hashCode() else 0)
        result = 31 * result + (if (parent !== this) parent.hashCode() else 0)
        return result
    }

}
