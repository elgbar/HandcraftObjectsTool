package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.event.EventTarget
import javafx.scene.Node

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * @author Elg
 */
interface ClassBuilder<out T> {

    val javaType: JavaType

    /**
     * The parent class builder, `null` if this is the root class builder or unknown parent
     */
    val parent: ClassBuilder<Any>?

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
    fun isLeaf(): Boolean {
        return getSubClassBuilders().isEmpty()
    }

    /**
     * Visual representation (and possibly modification) of this class builder
     */
    fun toView(par: EventTarget): Node

    /**
     * Note that this will a
     *
     * @return A class builder for the given property, or `null` for if [isLeaf] is `true`
     * @throws IllegalArgumentException If the given [name] is not valid
     */
    fun createClassBuilderFor(name: String): ClassBuilder<*>?

    /**
     * reset the given property for the [name] provided. If it has a default value this value will be restored
     *
     * @throws IllegalArgumentException If the given [name] is not valid
     */
    fun reset(name: String)

//    /**
//     * Add a property to the builder with the given key.
//     *
//     * If the [key] is `null` it is up to the implementation to specify how they are handled
//     *
//     * @throws IllegalArgumentException If `null` keys are not supported
//     */
//    operator fun set(key: String, value: Any?)
//
//    operator fun get(key: String): Any?


    /////////////////////////////////////
    //   JVM primitives (inc String)   //
    /////////////////////////////////////


    class ByteClassBuilder(initial: Byte = 0) : SimpleClassBuilder<Byte>(Byte::class.java, initial) {}

    class ShortClassBuilder(initial: Short = 0) : SimpleClassBuilder<Short>(Short::class.java, initial) {}

    class IntClassBuilder(initial: Int = 0) : SimpleClassBuilder<Int>(Int::class.java, initial) {}

    class LongClassBuilder(initial: Long = 0) : SimpleClassBuilder<Long>(Long::class.java, initial) {}

    class FloatClassBuilder(initial: Float = 0.0f) : SimpleClassBuilder<Float>(Float::class.java, initial) {}

    class DoubleClassBuilder(initial: Double = 0.0) : SimpleClassBuilder<Double>(Double::class.java, initial) {}

    class CharClassBuilder(initial: Char = '\u0000') : SimpleClassBuilder<Char>(Char::class.java, initial) {}

    /**
     * Note that the default value is the empty String `""` and not the default value `null`
     */
    class StringClassBuilder(initial: String = "") : SimpleClassBuilder<String>(String::class.java, initial) {}

    class BooleanClassBuilder(initial: Boolean = false) : SimpleClassBuilder<Boolean>(Boolean::class.java, initial) {}

    companion object {

        /**
         * Get a correct class builder for the given java type
         */
        fun getClassBuilder(type: JavaType, value: Any? = null): ClassBuilder<*> {
            if (value != null && value is ClassBuilder<*>) {
                //it would be very weird if this happened
                require(value.javaType == type) { "The value given is a already a class builder, but its type (${value.javaType}) does not match with the given java type $type" }
                return value

            } else if (type.isTypeOrSuperTypeOf(Byte::class.java)) {
                return if (value == null) ByteClassBuilder() else ByteClassBuilder(value as Byte)

            } else if (type.isTypeOrSuperTypeOf(Short::class.java)) {
                return if (value == null) ShortClassBuilder() else ShortClassBuilder(value as Short)

            } else if (type.isTypeOrSuperTypeOf(Int::class.java)) {
                return if (value == null) IntClassBuilder() else IntClassBuilder(value as Int)

            } else if (type.isTypeOrSuperTypeOf(Long::class.java)) {
                return if (value == null) LongClassBuilder() else LongClassBuilder(value as Long)

            } else if (type.isTypeOrSuperTypeOf(Float::class.java)) {
                return if (value == null) FloatClassBuilder() else FloatClassBuilder(value as Float)

            } else if (type.isTypeOrSuperTypeOf(Double::class.java)) {
                return if (value == null) DoubleClassBuilder() else DoubleClassBuilder(value as Double)

            } else if (type.isTypeOrSuperTypeOf(Char::class.java)) {
                return if (value == null) CharClassBuilder() else CharClassBuilder(value as Char)

            } else if (type.isTypeOrSuperTypeOf(String::class.java)) {
                return if (value == null) StringClassBuilder() else StringClassBuilder(value as String)

            } else if (type.isTypeOrSuperTypeOf(Boolean::class.java)) {
                return if (value == null) BooleanClassBuilder() else BooleanClassBuilder(value as Boolean)

            } else {
                //it's not a primitive type so let's just make a complex type for it
                return ComplexClassBuilder<Any>(type)
            }
        }
    }
}
