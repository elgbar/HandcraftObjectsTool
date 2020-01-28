package no.uib.inf219.gui.backend

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * @author Elg
 */
interface ClassBuilder<out T> {

    val clazz: Class<out T>

    /**
     * Convert this object to an instance of [T]
     */
    fun toObject(): T

    /**
     * Add a property to the builder with the given key.
     *
     * If the [key] is `null` it is up to the implementation to specify how they are handled
     *
     * @throws IllegalArgumentException If `null` keys are not supported
     */
    operator fun set(key: String, value: Any?)

    operator fun get(key: String): Any?

    companion object {

        /**
         * Create an object builder for the given class
         */
        inline fun <reified E> of(): ClassBuilder<E> {
            return MapClassBuilder(E::class.java)
        }
    }
}
