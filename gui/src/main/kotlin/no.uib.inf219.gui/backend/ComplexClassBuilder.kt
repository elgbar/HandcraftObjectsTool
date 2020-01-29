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
    override val javaType: JavaType,
    override val parent: ClassBuilder<Any>? = null
) : ClassBuilder<T> {

    private val propInfo = ClassInformation.serializableProperties(javaType)

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
        return SerializationManager.mapper.convertValue(objProp, javaType)
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>?> = props

    override fun createClassBuilderFor(name: String): ClassBuilder<*> {
        require(propInfo.contains(name)) { "The class $javaType does not have a property with the name '$name'. Expected one of the following: $propInfo" }

        val cb = ClassBuilder.getClassBuilder(propInfo[name]!!, null)
        props[name] = cb
        return cb
    }

    override fun reset(name: String) {
        require(propInfo.contains(name)) { "The class $javaType does not have a property with the name '$name'. Expected one of the following: $propInfo" }
        props[name] = null
    }
    

    override fun toView(par: EventTarget): Node {
        propList.clear()
        propList.addAll(props.toList())
        return par.vbox {
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
        return "MapClassBuilder(clazz=$javaType, props=${props.filter { it.value !== this }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexClassBuilder<*>) return false

        if (javaType != other.javaType) return false
        if (props.filter { it.value !== this } != other.props.filter { it.value !== this }) return false

        return true
    }


    override fun hashCode(): Int {
        var result = javaType.hashCode()
        result = 31 * result + props.filter { it !== this }.hashCode()
        return result
    }
}
