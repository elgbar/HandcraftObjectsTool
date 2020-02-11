package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.controllers.ObjectEditorController

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * @author Elg
 */
interface ClassBuilder<out T> {

    val type: JavaType

    /**
     * The parent class builder, `null` if this is the root class builder or unknown parent
     */
    val parent: ClassBuilder<*>?

    /**
     * Key of the property to access this from the parent (if any)
     */
    val name: String?

    /**
     * The property this class builder is creating, used for gaining additional metadata about what we're creating.
     */
    val property: PropertyWriter?

    /**
     * Convert this object to an instance of [T]
     */
    fun toObject(): T?

    /**
     * return all sub values this class can hold. The [Map.keys] must exactly return all valid keys. If any of them have a default value they must reflect so
     *
     * Empty if [isLeaf] is true
     */
    fun getSubClassBuilders(): Map<String, ClassBuilder<*>?>

    /**
     * If this implementation does not have any sub class builders
     */
    fun isLeaf(): Boolean

    /**
     * Visual representation (and possibly modification) of this class builder
     */
    fun toView(parent: EventTarget, controller: ObjectEditorController): Node

    /**
     * Note that this will a
     *
     * @return A class builder for the given property, or `null` for if [isLeaf] is `true`
     * @throws IllegalArgumentException If the given [property] is not valid
     */
    fun createClassBuilderFor(property: String): ClassBuilder<*>?


    /**
     * Reset the given property for the [property] provided. If it has a default value this value will be restored
     *
     * @return If all referenced should be null-ed out
     */
    fun reset(property: String): ClassBuilder<*>? {
        return reset(property, null)
    }

    /**
     * Reset the given property for the [property] provided. If it has a default value this value will be restored
     *
     *
     * @return The new (potentially null) classbuilder, might be equal to [element]
     *
     * @throws IllegalArgumentException If both [property] and [element] are `null`
     */
    fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>?

    /**
     * Preview of this class
     */
    fun previewValue(): String

    fun getClassBuilder(
        type: JavaType,
        name: String,
        value: Any? = null,
        prop: PropertyWriter? = null
    ): ClassBuilder<*>? {
        return getClassBuilder(type, this, name, value, prop)
    }

    /**
     * @return If we are a forefather of the given [ClassBuilder]
     */
    fun isParent(to: ClassBuilder<*>?): Boolean {
        return when {
            to == null -> false
            this == to.parent -> false
            else -> this.isParent(to.parent)
        }
    }

    /**
     * If this class builder is required to be valid. If [property] is `null` this is assumed to be required.
     */
    fun isRequired(): Boolean {
        return property?.isRequired ?: true
    }

    /////////////////////////////////////
    //   JVM primitives (inc String)   //
    /////////////////////////////////////

    class ByteClassBuilder(
        initial: Byte = 0,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Byte>(Byte::class.java, initial, parent, name, prop) {}

    class ShortClassBuilder(
        initial: Short = 0,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Short>(Short::class.java, initial, parent, name, prop) {}

    class IntClassBuilder(
        initial: Int = 0,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Int>(Int::class.java, initial, parent, name, prop) {}

    class LongClassBuilder(
        initial: Long = 0,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Long>(Long::class.java, initial, parent, name, prop) {}

    class FloatClassBuilder(
        initial: Float = 0.0f,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Float>(Float::class.java, initial, parent, name, prop) {}

    class DoubleClassBuilder(
        initial: Double = 0.0,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Double>(Double::class.java, initial, parent, name, prop) {}

    class CharClassBuilder(
        initial: Char = '\u0000',
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Char>(Char::class.java, initial, parent, name, prop) {}

    /**
     * Note that the default value is the empty String `""` and not the default value `null`
     */
    class StringClassBuilder(
        initial: String = "",
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<String>(String::class.java, initial, parent, name, prop) {}

    class BooleanClassBuilder(
        initial: Boolean = false,
        parent: ClassBuilder<*>? = null,
        name: String? = null,
        prop: PropertyWriter? = null
    ) :
        SimpleClassBuilder<Boolean>(Boolean::class.java, initial, parent, name, prop) {}


    companion object {

        /**
         * Get a correct class builder for the given java type
         */
        fun getClassBuilder(
            type: JavaType,
            parent: ClassBuilder<*>? = null,
            name: String?,
            value: Any? = null,
            prop: PropertyWriter? = null
        ): ClassBuilder<*>? {
            return if (value != null && value is ClassBuilder<*>) {
                //it would be very weird if this happened
                require(value.type == type) { "The value given is a already a class builder, but its type (${value.type}) does not match with the given java type $type" }
                return value
            } else if (type.isPrimitive) {
                when {
                    type.isTypeOrSuperTypeOf(Byte::class.java) -> {
                        if (value == null) ByteClassBuilder(parent = parent) else
                            ByteClassBuilder(value as Byte, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Short::class.java) -> {
                        if (value == null) ShortClassBuilder(parent = parent) else
                            ShortClassBuilder(value as Short, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Int::class.java) -> {
                        if (value == null) IntClassBuilder(parent = parent) else
                            IntClassBuilder(value as Int, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Long::class.java) -> {
                        if (value == null) LongClassBuilder(parent = parent) else
                            LongClassBuilder(value as Long, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Float::class.java) -> {
                        if (value == null) FloatClassBuilder(parent = parent) else
                            FloatClassBuilder(value as Float, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Double::class.java) -> {
                        if (value == null) DoubleClassBuilder(parent = parent) else
                            DoubleClassBuilder(value as Double, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Char::class.java) -> {
                        if (value == null) CharClassBuilder(parent = parent) else
                            CharClassBuilder(value as Char, parent, name, prop)
                    }
                    type.isTypeOrSuperTypeOf(Boolean::class.java) -> {
                        if (value == null) BooleanClassBuilder(parent = parent) else
                            BooleanClassBuilder(value as Boolean, parent, name, prop)
                    }
                    else -> throw IllegalStateException("Unknown primitive $type")
                }
            } else if (type.isTypeOrSuperTypeOf(String::class.java)) {
                if (value == null) StringClassBuilder(parent = parent) else
                    StringClassBuilder(value as String, parent, name, prop)
            } else if (type.isCollectionLikeType) {
                CollectionClassBuilder<Any>(type as CollectionLikeType, parent, name, prop)
            } else if (type.isMapLikeType) {
                TODO("Maps are not yet supported: $type")
            } else if (!type.isConcrete) {
                return null
//                TODO("Selection of concrete subclasses are not yet supported: $type")
            } else {
                //it's not a primitive type so let's just make a complex type for it
                ComplexClassBuilder<Any>(type, parent, name, prop)
            }
        }
    }
}
