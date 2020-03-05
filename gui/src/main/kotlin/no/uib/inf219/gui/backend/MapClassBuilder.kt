package no.uib.inf219.gui.backend


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.serializers.ClassBuilderCompiler
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.PropertyEditor
import tornadofx.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * @author Elg
 */
//@JsonSerialize(using = MapCBSerializer::class)
open class MapClassBuilder<K, out V>(
    override val type: JavaType,
    override val name: String,
    override val parent: ClassBuilder<*>?,
    override val property: PropertyWriter?
) : ReferencableClassBuilder<Map<K?, V?>>() {

    override val serObject: MutableMap<ClassBuilder<*>, ClassBuilder<*>?> = HashMap()

    override fun compile(cbs: ClassBuilderCompiler): Map<Any, Any?> {
        return serObject.mapKeys { (key, _) -> cbs.compile(key) }.mapValues { (_, value) ->
            if (value != null) {
                cbs.compile(value)
            } else {
                null
            }
        }
    }

    override fun link(cbs: ClassBuilderCompiler, obj: Any) {
        require(obj is MutableMap<*, *>) { "Cannot link a map class builder with object other than mutable Map" }
        @Suppress("UNCHECKED_CAST")
        val objMap: MutableMap<Any, Any?> = obj as MutableMap<Any, Any?>

        for ((key, value) in objMap) {
            if (value != null) {
                objMap[key] = cbs.resolveReference(value)
            }
        }

        TODO("also remap keys")

//        return obj.mapKeys { (key, _) ->
//            requireNotNull(key)
//            cbs.resolveReference(key)
//        }.mapValues { (_, ref) ->
//            if (ref != null) {
//                cbs.resolveReference(ref)
//            } else {
//                null
//            }
//        }
    }

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.splitpane {
            setDividerPositions(0.25)
            val con = ObjectEditorController(type, this@MapClassBuilder, controller)
            this += vbox {
                button("Add element") {
                    action {
                        val key = getClassBuilder(type.keyType, "key #${serObject.size}") ?: return@action
                        val value =
                            getClassBuilder(type.contentType, "value #${serObject.size}") ?: return@action
                        serObject[key] = value
                        controller.reloadView()
                        recompile()
                    }
                }
                for ((key, value) in serObject) {
                    hbox {
                        style { addClass(Styles.parent) }
                        val kname = key.name
                        val vname = value?.name ?: "null"
                        button(kname) { action { con.select(kname, key) } }
                        button(vname) { action { con.select(vname, value) } }
                    }
                }
            }
            this += find<PropertyEditor>(params = *arrayOf("controller" to con)).root
        }
    }

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? {
        return serObject.computeIfAbsent(key) { init }
    }

    override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?) {
        //The map must have the given key
        require(serObject.containsKey(key)) { "Given key does not exist in this map class builder" }
        //But does the given element is allowed to be null,
        require(element == null || serObject[key] == element) { "Given value does not match with this map class builder's value of given key" }

        //if either key or value (if not null) should be removed
        // we will remove all bindings. Though if the element is null
        val remove = key.reset() || element?.reset() ?: false
        if (remove) serObject.remove(key)
    }


    override fun getPreviewValue(): String {
        return serObject.map { it.key.getPreviewValue() + " -> " + it.value?.getPreviewValue() }
            .joinToString(", ")
    }

    override fun getChildType(cb: ClassBuilder<*>): JavaType? {
        //TODO what if we want to reference a key?
        return type.contentType
    }

    override fun reset(): Boolean = true

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> = serObject

    override fun isLeaf(): Boolean = false

    override fun isImmutable(): Boolean = false

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

    override fun toString(): String {
        return "Map CB; key type=${type.keyType}, contained type=${type.contentType})"
    }
}
