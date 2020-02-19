package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.beans.Observable
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.OutputArea
import tornadofx.fold
import tornadofx.scrollpane
import tornadofx.squeezebox
import tornadofx.toObservable
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * A class builder intended to be used for normal classes. It is 'complex' due containing multiple other [ClassBuilder]s.
 *
 * @author Elg
 */
class ComplexClassBuilder<out T>(
    override val type: JavaType,
    override val name: String,
    override val parent: ClassBuilder<*>? = null,
    override val property: PropertyWriter? = null
) : ClassBuilder<T> {

    private val propInfo = ClassInformation.serializableProperties(type)
    private val propDefaults: MutableMap<String, Any?> = HashMap()

    private val props: MutableMap<String, ClassBuilder<*>?> = HashMap()
    private val obProp: ObservableMap<String, ClassBuilder<*>?> = props.toObservable()

    private val propList: MutableList<Pair<String, ClassBuilder<*>?>> = ArrayList()
    private val obPropList: ObservableList<Pair<String, ClassBuilder<*>?>> = propList.toObservable()

    init {
        //initiate all valid values to null
        // to allow for iteration when populating Node explorer
        for ((k, v) in propInfo) {
            val propAn = v.getAnnotation(JsonProperty::class.java)
            val default: Any? =
                if (propAn != null) {
                    val defaultStr = propAn.defaultValue
                    if (defaultStr.isEmpty()) {
                        null
                    } else {
                        try {
                            ControlPanelView.mapper.readValue(defaultStr, v.type) as Any?
                        } catch (e: Throwable) {
                            OutputArea.logln("Failed to parse default value for property $k of $type. Given string '$defaultStr'")
                            OutputArea.logln(e.localizedMessage)
                            null
                        }
                    }
                } else null
            propDefaults[k] = default
            createClassBuilderFor(k)
        }

        obProp.addListener { ob: Observable ->
            check(ob is ObservableMap<*, *>)
            propList.clear()
            propList.addAll(props.toList())

        }
    }

    override fun toObject(): T? {
        val objProp = props.mapValues { it.value?.toObject() }
        return ControlPanelView.mapper.convertValue(objProp, type)
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>?> = props

    override fun createClassBuilderFor(property: String): ClassBuilder<*>? {
        val prop = propInfo[property]
        require(prop != null) { "The class $type does not have a property with the name '$property'. Expected one of the following: $propInfo" }

        return props.computeIfAbsent(property) {
            @Suppress("MapGetWithNotNullAssertionOperator") //checked above
            getClassBuilder(prop.type, it, propDefaults[it], prop)
        }
    }

    override fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>? {
        require(propInfo.contains(property)) { "The class $type does not have a property with the name '$property'. Expected one of the following: $propInfo" }

        props[property] = null
        return createClassBuilderFor(property)
    }

    override fun isLeaf(): Boolean {
        return false
    }

    override fun toView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        obPropList.clear()
        obPropList.addAll(props.toList().sortedBy { it.first })
        return parent.scrollpane(fitToWidth = true, fitToHeight = true).squeezebox {
            for ((name, cb) in obPropList) {
                if (cb != null && cb.isLeaf())
                    fold("$name ${cb.previewValue()}") {
                        cb.toView(this, controller)
                    }
            }
            //            tableview(obPropList) {
//                columnResizePolicy = SmartResize.POLICY
//                column("Name") { it: TableColumn.CellDataFeatures<Pair<String, ClassBuilder<*>?>, String> ->
//                    it.value.first.toProperty()
//                }
//                column("Value") { it: TableColumn.CellDataFeatures<Pair<String, ClassBuilder<*>?>, String> ->
//                    it.value.second?.previewValue().toProperty()
//                }
//                column("Type") { it: TableColumn.CellDataFeatures<Pair<String, ClassBuilder<*>?>, String> ->
//                    it.value.second?.type?.typeName.toProperty()
//                }
//                onDoubleClick {
//                    val clicked = this.selectedItem ?: return@onDoubleClick
//                    controller.select(clicked)
//                }
//            }
        }
    }

    override fun previewValue(): String {
        return props.filter { !this.isParent(it.value) }.map { "${it.key}: ${it.value?.previewValue()}" }
            .joinToString("\n")
    }

    override fun toString(): String {
        return "MapClassBuilder(clazz=$type, props=${props.filter { it.value !== this }})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComplexClassBuilder<*>) return false

        if (type != other.type) return false
        if (name != other.name) return false
        if (parent != other.parent) return false
        if (property != other.property) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + (property?.hashCode() ?: 0)
        return result
    }


}
