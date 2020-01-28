package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.textarea

/**
 * A class builder intended to be used for normal classes. It is 'complex' due containing multiple other [ClassBuilder]s.
 *
 * @author Elg
 */
class ComplexClassBuilder<out T>(override val clazz: JavaType) : ClassBuilder<T> {

    private val props: MutableMap<String, ClassBuilder<*>> = HashMap()
    private val propInfo = ClassInformation.serializableProperties(clazz)

    init {
        for ((k, v) in propInfo) {
            props[k] = ClassBuilder.getClassBuilder(v)
        }
    }

    override fun toObject(): T {
        val objProp = props.mapValues { it.value.toObject() }
        return SerializationManager.mapper.convertValue(objProp, clazz)
    }

    override fun getValidKeys(): Set<String> = propInfo.keys

    override fun set(key: String, value: Any) {
        require(propInfo.contains(key)) { "The class $clazz does not have a property with the name '$key'. Expected one of the following: $propInfo" }
        val cb: ClassBuilder<*> = ClassBuilder.getClassBuilder(propInfo[value]!!, value)
        props[key] = cb
    }

    override fun get(key: String): Any {
        return props[key]!!
    }

    override fun isLeaf(): Boolean {
        return false
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>> = props


    override fun toString(): String {
        return "MapClassBuilder(clazz=$clazz, props=${props.filter { it.value !== this }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexClassBuilder<*>) return false

        if (clazz != other.clazz) return false
        if (props.filter { it.value !== this } != other.props.filter { it.value !== this }) return false

        return true
    }


    override fun hashCode(): Int {
        var result = clazz.hashCode()
        result = 31 * result + props.filter { it !== this }.hashCode()
        return result
    }

    override fun toView(par: EventTarget): Node {
        return par.textarea("tetetet")
    }

}
