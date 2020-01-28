package no.uib.inf219.gui.backend

import no.uib.inf219.api.serialization.SerializationManager

/**
 * @author Elg
 */
class MapClassBuilder<out T>(override val clazz: Class<out T>) : ClassBuilder<T> {

    private val props: MutableMap<String, Any?> = HashMap()

    override fun toObject(): T {
        val objProp = props.mapValues {
            when (it.value) {
                //if a property value is a ObjectBuilder convert it to its T
                is ClassBuilder<*> -> (it.value as ClassBuilder<*>).toObject()

                //build a map as sub object
                is Map<*, *> -> (it.value as Map<*, *>).mapValues { elem ->
                    if (elem.value is ClassBuilder<*>) (elem.value as ClassBuilder<*>).toObject() else elem.value
                }
                //convert any ObjectBuilder to its T for in collections like List, Set etc
                is Iterable<*> -> (it.value as Iterable<*>).map { elem: Any? -> if (elem is ClassBuilder<*>) elem.toObject() else elem }
                else -> it.value
            }
        }
        println("objProp = ${objProp}")
        @Suppress("UNCHECKED_CAST")
        return SerializationManager.loadFromMap(objProp, clazz) as T
    }

    override fun set(key: String, value: Any?) {
        props[key] = value
    }

    override fun get(key: String): Any? {

        return props[key]
    }
}
