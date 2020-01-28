package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.gui.backend.ClassBuilder.*
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.textarea

/**
 * @author Elg
 */
class MapClassBuilder<out T>(override val clazz: JavaType) : ClassBuilder<T> {

    private val props: MutableMap<String, ClassBuilder<*>> = HashMap()
    private val propInfo = ClassInformation.serializableProperties(clazz)

    init {
        for ((k, v) in propInfo) {
            props[k] = CBFromJC(v)
        }
    }

    override fun toObject(): T {
        val objProp = props.mapValues { it.value.toObject() }
        return SerializationManager.mapper.convertValue(objProp, clazz)
    }

    override fun getValidKeys(): Set<String> = propInfo.keys

    override fun set(key: String, value: Any) {
        require(propInfo.contains(key)) { "The class $clazz does not have a property with the name '$key'. Expected one of the following: $propInfo" }
        val cb: ClassBuilder<*> = CBFromJC(propInfo[value]!!, value)
        props[key] = cb
    }

    override fun get(key: String): Any {
        return props[key]!!
    }

    override fun isLeaf(): Boolean {
        return false
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>> = props

    fun CBFromJC(jc: JavaType, value: Any? = null): ClassBuilder<*> {
        if (value != null && value is ClassBuilder<*>) return value
        else if (jc == ClassInformation.toJavaType(String::class.java)) {
            return if (value == null) StringClassBuilder() else StringClassBuilder(value as String)

        } else if (jc == ClassInformation.toJavaType(Byte::class.java)) {
            return if (value == null) ByteClassBuilder() else ByteClassBuilder(value as Byte)

        } else if (jc == ClassInformation.toJavaType(Short::class.java)) {
            return if (value == null) ShortClassBuilder() else ShortClassBuilder(value as Short)

        } else if (jc == ClassInformation.toJavaType(Int::class.java)) {
            return if (value == null) IntClassBuilder() else IntClassBuilder(value as Int)

        } else if (jc == ClassInformation.toJavaType(Long::class.java)) {
            return if (value == null) LongClassBuilder() else LongClassBuilder(value as Long)
        } else {
            return MapClassBuilder<Any?>(jc)
        }
    }


    override fun toString(): String {
        return "MapClassBuilder(clazz=$clazz, props=${props.filter { it.value !== this }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapClassBuilder<*>) return false

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
