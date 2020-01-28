package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.textarea

/**
 * @author Elg
 */
abstract class SimpleClassBuilder<T> internal constructor(primClass: Class<T>, initialValue: T) : ClassBuilder<T> {

    override val clazz: JavaType = ClassInformation.toJavaType(primClass)

    private val valueProperty by lazy { SimpleObjectProperty<T>() }
    fun valueProperty() = valueProperty
    var value: T
        get() = valueProperty.get()
        set(value) = valueProperty.set(value)

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
        return value!!
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
        var result = value?.hashCode() ?: 0
        result = 31 * result + clazz.hashCode()
        return result
    }

    override fun toString(): String {
        return "SimpleClassBuilder(value=$value, clazz=$clazz)"
    }

    override fun toView(par: EventTarget): Node {

        return par.textarea {

        }
    }

}
