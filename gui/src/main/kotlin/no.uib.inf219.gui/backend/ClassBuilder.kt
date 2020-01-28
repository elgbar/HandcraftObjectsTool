package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.bind
import tornadofx.textarea

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

    class StringClassBuilder(initial: String = "") : SimpleClassBuilder<String>(String::class.java, initial) {

        override fun toView(par: EventTarget): Node {
            return par.textarea {
                bind(this@StringClassBuilder.valueProperty())
            }
        }
    }

    class ByteClassBuilder(initial: Byte = 0) : SimpleClassBuilder<Byte>(Byte::class.java, initial) {}

    class ShortClassBuilder(initial: Short = 0) : SimpleClassBuilder<Short>(Short::class.java, initial) {}

    class IntClassBuilder(initial: Int = 0) : SimpleClassBuilder<Int>(Int::class.java, initial) {}

    class LongClassBuilder(initial: Long = 0) : SimpleClassBuilder<Long>(Long::class.java, initial) {}

    class DoubleClassBuilder(initial: Double = 0.0) : SimpleClassBuilder<Double>(Double::class.java, initial) {}
}
