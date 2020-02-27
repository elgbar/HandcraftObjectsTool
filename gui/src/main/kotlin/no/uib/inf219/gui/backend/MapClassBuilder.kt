package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.PropertyEditor
import tornadofx.*
import kotlin.collections.set

/**
 * @author Elg
 */
class MapClassBuilder<K, out V>(
    override val type: MapLikeType,
    override val name: String,
    override val parent: ClassBuilder<*>?,
    override val property: PropertyWriter?
) : ClassBuilder<Map<K?, V?>> {


    val map: MutableMap<ClassBuilder<*>, ClassBuilder<*>> = HashMap()

    override fun toTree(): JsonNode {
        return ControlPanelView.mapper.valueToTree(map.mapValues { it.value.toTree() }.mapKeys { it.key.toTree() })
    }

    override fun toObject(): Map<K?, V?> {
        val realMap = HashMap<K?, V?>()

        for (entry in map) {
            val key = entry.key.toObject() as K?
            val value = entry.value.toObject() as V?
            realMap[key] = value
        }

        return realMap
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>?> {
        return emptyMap()
    }

    override fun isLeaf(): Boolean {
        return false
    }

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.splitpane {
            setDividerPositions(0.25)
            val con = ObjectEditorController(type, this@MapClassBuilder, controller)
            this += vbox {
                button("Add element") {
                    action {
                        val key = getClassBuilder(type.keyType, "key #${map.size}") ?: return@action
                        val value = getClassBuilder(type.contentType, "value #${map.size}") ?: return@action
                        map[key] = value
                        controller.reloadView()
                    }
                }
                for ((key, value) in map) {
                    hbox {
                        style { addClass(Styles.parent) }
                        val kname = key.name
                        val vname = value.name
                        button(kname) { action { con.select(kname, key) } }
                        button(vname) { action { con.select(vname, value) } }
                    }
                }
            }
            this += find<PropertyEditor>(params = *arrayOf("controller" to con)).root
        }
    }

    override fun createClassBuilderFor(property: String): ClassBuilder<*>? {
        return null
    }

    override fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>? {
        return null
    }

    override fun previewValue(): String {
        return map.map { it.key.previewValue() + " -> " + it.value.previewValue() }.joinToString(", ")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapClassBuilder<*, *>) return false

        if (type != other.type) return false
        if (parent != other.parent) return false
        if (name != other.name) return false
        if (property != other.property) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + (property?.hashCode() ?: 0)
        return result
    }


}
