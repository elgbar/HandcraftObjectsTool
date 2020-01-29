package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.Observable
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TableColumn
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.*
import kotlin.collections.set

/**
 * A class builder intended to be used for normal classes. It is 'complex' due containing multiple other [ClassBuilder]s.
 *
 * @author Elg
 */
class ComplexClassBuilder<out T>(
    override val type: JavaType,
    override val parent: ClassBuilder<*>? = null
) : ClassBuilder<T> {

    private val propInfo = ClassInformation.serializableProperties(type)

    private val props: MutableMap<String, ClassBuilder<*>?> = HashMap()
    private val obProp: ObservableMap<String, ClassBuilder<*>?> = props.toObservable()

    private val propList: MutableList<Pair<String, ClassBuilder<*>?>> = ArrayList()
    private val obPropList: ObservableList<Pair<String, ClassBuilder<*>?>> = propList.toObservable()

    init {
        //initiate all valid values to null
        // to allow for iteration when populating Node explorer
        for ((k, _) in propInfo) {
            props[k] = null
        }

        obProp.addListener { ob: Observable ->
            check(ob is ObservableMap<*, *>)
            println("propInfo = $propInfo")
            propList.clear()
            propList.addAll(props.toList())

        }
    }

    override fun toObject(): T? {
        val objProp = props.mapValues { it.value?.toObject() }
        return SerializationManager.mapper.convertValue(objProp, type)
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>?> = props

    override fun createClassBuilderFor(property: String): ClassBuilder<*> {
        require(propInfo.contains(property)) { "The class $type does not have a property with the name '$property'. Expected one of the following: $propInfo" }

        return props.computeIfAbsent(property) {
            @Suppress("MapGetWithNotNullAssertionOperator") //checked above
            getClassBuilder(propInfo[it]!!.type)
        }!!
    }

    override fun reset(name: String): Boolean {
        require(propInfo.contains(name)) { "The class $javaType does not have a property with the name '$name'. Expected one of the following: $propInfo" }
        props[name] = null
        return true
    }

    override fun toView(parent: EventTarget): Node {
        propList.clear()
        propList.addAll(props.toList())
        return parent.vbox {
            tableview(obPropList) {
                column("Name") { it: TableColumn.CellDataFeatures<Pair<String, ClassBuilder<*>?>, String> ->
                    it.value.first.toProperty()
                }
                column("Value") { it: TableColumn.CellDataFeatures<Pair<String, ClassBuilder<*>?>, String> ->
                    it.value.second.toString().toProperty()
                }
            }
        }
    }

    override fun toString(): String {
        return "MapClassBuilder(clazz=$type, props=${props.filter { it.value !== this }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexClassBuilder<*>) return false

        if (type != other.type) return false
        if (props.filter { it.value !== this } != other.props.filter { it.value !== this }) return false

        return true
    }


    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + props.filter { it !== this }.hashCode()
        return result
    }

    override fun defaultValue(property: String): Any? {
        TODO("not implemented")
    }
}
