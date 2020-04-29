package no.uib.inf219.gui.backend


import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import no.uib.inf219.extra.reload
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.serializers.MapClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.controllers.classBuilderNode.EmptyClassBuilderNode
import no.uib.inf219.gui.controllers.classBuilderNode.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView.mapper
import tornadofx.action
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.button

/**
 * @author Elg
 */
@JsonSerialize(using = MapClassBuilderSerializer::class, keyUsing = MapClassBuilderSerializer::class)
class MapClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata?,
    override val item: TreeItem<ClassBuilderNode>
) : ParentClassBuilder() {

    override val serObject = HashSet<ComplexClassBuilder>()
    override val serObjectObservable = serObject.asObservable()

    private val entryType =
        mapper.typeFactory.constructMapLikeType(MapEntry::class.java, type.keyType, type.contentType)

    private var entriesCreated = 0

    companion object {
        const val ENTRY = "entry"
        const val ENTRY_KEY = "key"
        const val ENTRY_VALUE = "value"

        val keyCb = ENTRY_KEY.toCb()
        val valueCb = ENTRY_VALUE.toCb()

        val entryTypeCB = mapper.typeFactory.constructType(object :
            TypeReference<Map.Entry<ClassBuilder?, ClassBuilder?>>() {})
            ?: error("Failed to construct map entry type")

        data class MapEntry<K, V>(override val key: K?, override val value: V?) : Map.Entry<K?, V?> {}
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
        item: TreeItem<ClassBuilderNode>
    ): ComplexClassBuilder {
        val entryCb = "$ENTRY ${entriesCreated++}".toCb()
        val entry = ComplexClassBuilder(
            entryType, entryCb, this, getChildPropertyMetadata(entryCb), item
        )
        item.value = FilledClassBuilderNode(entryCb, entry, this, allowReference = false)

        val keyNode = EmptyClassBuilderNode(keyCb, entry, allowReference = false)
        val valueNode = EmptyClassBuilderNode(valueCb, entry, allowReference = true)
        
        item.children.setAll(listOf(keyNode, valueNode).map { it.item })

        serObject += entry
        this.item.children.add(entry.item)
        return entry
    }

    private fun remove(key: ClassBuilder?): Boolean {
        val entry = get(key) ?: return false
        serObject.remove(key)
        return serObject.remove(entry)
    }

    private fun createNewChild(controller: ObjectEditorController) {
        val created = create(TreeItem())
        controller.tree.reload()
        item.isExpanded = true
        created.item.isExpanded = true
    }

    override fun onNodeClick(
        event: MouseEvent,
        controller: ObjectEditorController
    ) {
        if (event.clickCount == 2 && event.button == MouseButton.PRIMARY) {
            createNewChild(controller)
            event.consume()
        }
    }

    override fun createEditView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.borderpane {
            center = button("Add entry") {
                action {
                    createNewChild(controller)
                }
            }
        }
    }

    override fun createChildClassBuilder(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder? {
        require(init == null || init.type == getChildType(key)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
        }
        return if (!contains(key)) {
            create(item)
        } else {
            get(key)
        }
    }

    override fun getChild(key: ClassBuilder): ClassBuilder? {
        return get(key)
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ) {
        //The map must have the given key
        require(contains(key)) { "Given key does not exist in this map class builder" }
        //But does the given element is allowed to be null,
        require(element == null || get(key) == element) { "Given value does not match with this map class builder's value of given key" }

        val childItem = get(key)!!.item
        remove(key)
        item.children.remove(childItem)
    }

    override fun getPreviewValue(): String {
        return "Map<${type.keyType}, ${type.contentType}> of size ${serObject.size}"
    }

    override fun getChildPropertyMetadata(key: ClassBuilder) = ClassInformation.PropertyMetadata(
        key.getPreviewValue(),
        entryType,
        "",
        false,
        "An entry in a map",
        true
    )

    override fun getChildType(key: ClassBuilder): JavaType? {
        return entryType
    }

    override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder?> {
        //TODO fix this somehow. It does not really follow the interface requirement, can can never do so as the key is nullable
        // However as it is only used by the interface in getChildren and getTreeItems those are for now overwritten
        // but this is of course not maintainable free
        return serObject.map { cb -> cb.key to cb }.toMap()
    }

    override fun isImmutable(): Boolean = false

    override fun toString(): String {
        return "Map CB; key type=${type.keyType}, contained type=${type.contentType})"
    }
}
