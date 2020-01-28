package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.loader.ClassInformation

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * @author Elg
 */
interface ClassBuilder<out T> {

    val clazz: JavaType

    /**
     * Convert this object to an instance of [T]
     */
    fun toObject(): T?

    /**
     * A set of all valid keys this class builder can have. If empty all keys are allowed
     */
    fun getValidKeys(): Set<String>

    /**
     * return all sub values this class can hold
     *
     * Empty if [isLeaf] is true
     */
    fun getSubClassBuilders(): Map<String, ClassBuilder<*>>

    /**
     * If this implementation is a final value
     */
    fun isLeaf(): Boolean

    fun toView(par: EventTarget): Node


    /**
     * Add a property to the builder with the given key.
     *
     * If the [key] is `null` it is up to the implementation to specify how they are handled
     *
     * @throws IllegalArgumentException If `null` keys are not supported
     */
    operator fun set(key: String, value: Any)

    operator fun get(key: String): Any

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
                require(value.clazz == type) { "The value given is a already a class builder, but its type (${value.clazz}) does not match with the given java type $type" }
                return value

            } else if (type == ClassInformation.toJavaType(Byte::class.java)) {
                return if (value == null) ByteClassBuilder() else ByteClassBuilder(value as Byte)

            } else if (type == ClassInformation.toJavaType(Short::class.java)) {
                return if (value == null) ShortClassBuilder() else ShortClassBuilder(value as Short)

            } else if (type == ClassInformation.toJavaType(Int::class.java)) {
                return if (value == null) IntClassBuilder() else IntClassBuilder(value as Int)

            } else if (type == ClassInformation.toJavaType(Long::class.java)) {
                return if (value == null) LongClassBuilder() else LongClassBuilder(value as Long)

            } else if (type == ClassInformation.toJavaType(Float::class.java)) {
                return if (value == null) FloatClassBuilder() else FloatClassBuilder(value as Float)

            } else if (type == ClassInformation.toJavaType(Double::class.java)) {
                return if (value == null) DoubleClassBuilder() else DoubleClassBuilder(value as Double)

            } else if (type == ClassInformation.toJavaType(Char::class.java)) {
                return if (value == null) CharClassBuilder() else CharClassBuilder(value as Char)

            } else if (type == ClassInformation.toJavaType(String::class.java)) {
                return if (value == null) StringClassBuilder() else StringClassBuilder(value as String)

            } else if (type == ClassInformation.toJavaType(Boolean::class.java)) {
                return if (value == null) BooleanClassBuilder() else BooleanClassBuilder(value as Boolean)

            } else {
                //it's not a primitive type so let's just make a complex type for it
                return ComplexClassBuilder<Any>(type)
            }
        }
    }
}
