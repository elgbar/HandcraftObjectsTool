package no.uib.inf219.gui.backend


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.serializers.MapClassBuilderSerializer
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.controllers.FilledClassBuilderNode
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView.mapper
import tornadofx.action
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.button
import kotlin.collections.set

/**
 * @author Elg
 */
@JsonSerialize(using = MapClassBuilderSerializer::class)
class MapClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata?,
    override val item: TreeItem<ClassBuilderNode>
) : ParentClassBuilder() {

    override val serObject = HashSet<ComplexClassBuilder>()
    override val serObjectObservable = serObject.asObservable()

    companion object {
        const val ENTRY_KEY = "key"
        const val ENTRY_VALUE = "value"
        val keyCb = ENTRY_KEY.toCb()
        val valueCb = ENTRY_VALUE.toCb()

        val entryCb = "entry".toCb()

        val entryType = mapper.typeFactory.constructType(object :
            TypeReference<Map.Entry<ClassBuilder?, ClassBuilder?>>() {})
            ?: error("Failed to construct map entry type")
    }

    private fun get(key: ClassBuilder?): ClassBuilder? {
        return serObject.firstOrNull { it.key == key }
    }

    /**
     * Check the if an entry with the given key exists
     */
    private fun contains(key: ClassBuilder?): Boolean {
        return get(key) != null
    }

    private fun create(
        key: ClassBuilder,
        value: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ComplexClassBuilder {
        val entry = ComplexClassBuilder(entryType, entryCb, this@MapClassBuilder, item = item)
        item.value = FilledClassBuilderNode(key, entry, parent)

        entry.serObject[ENTRY_VALUE] = value
        entry.serObject[ENTRY_KEY] = key
        serObject += entry
        return entry
    }

    private fun remove(key: ClassBuilder?): Boolean {
        val entry = get(key) ?: return false
        serObject.remove(key)
        return serObject.remove(entry)
    }

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {

        return parent.borderpane {
            center = button("Add entry") {
                action {

                    val key = getClassBuilder(type.keyType, keyCb) ?: return@action
                    val value = getClassBuilder(type.contentType, valueCb) ?: return@action
                    create(key, value, item)
                    controller.reloadView()
                }
            }
        }
    }

    override fun createChildClassBuilder(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder {
        require(init == null || init.type == getChildType(key)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
        }
        return if (!contains(key)) {
            create(key, init, item)
        } else {
            get(key)!!
        }
    }

    override fun getChild(key: ClassBuilder): ClassBuilder? {
        return get(key)
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ): ClassBuilderNode? {
        //The map must have the given key
        require(contains(key)) { "Given key does not exist in this map class builder" }
        //But does the given element is allowed to be null,
        require(element == null || get(key) == element) { "Given value does not match with this map class builder's value of given key" }

        remove(key)
        return null
    }

    override fun getPreviewValue(): String {
        return "Map<${type.keyType}, ${type.contentType}> of size ${serObject.size}"
    }

    override fun getChildType(cb: ClassBuilder): JavaType? {
        return entryType
    }

    override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder?> {
        //TODO fix this somehow. It does not really follow the interface requirement, can can never do so as the key is nullable
        // However as it is only used by the interface in getChildren and getTreeItems those are for now overwritten
        // but this is of course not maintainable free
        return serObject.mapIndexed { index, cb -> index.toCb() to cb }.toMap()
    }

    override fun getChildren(): List<ClassBuilder> {
        return serObject.toList()
    }

    override fun getTreeItems(): List<ClassBuilderNode> {
        return serObject.map { FilledClassBuilderNode(entryCb, it, this) }
    }

    override fun isImmutable(): Boolean = false

    override fun toString(): String {
        return "Map CB; key type=${type.keyType}, contained type=${type.contentType})"
    }
}
