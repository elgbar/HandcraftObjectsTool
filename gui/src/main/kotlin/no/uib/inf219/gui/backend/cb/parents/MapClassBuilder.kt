package no.uib.inf219.gui.backend.cb.parents


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.VariableSizedParentClassBuilder
import no.uib.inf219.gui.backend.cb.serializers.MapClassBuilderSerializer
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.EmptyClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView.mapper
import tornadofx.asObservable

/**
 * The class builder variant for [Map]s.
 *
 * @author Elg
 */
@JsonSerialize(using = MapClassBuilderSerializer::class, keyUsing = MapClassBuilderSerializer::class)
class MapClassBuilder(
    override val type: MapLikeType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata?,
    override val item: TreeItem<ClassBuilderNode>
) : VariableSizedParentClassBuilder() {

    override val serObject = HashSet<ComplexClassBuilder>()
    override val serObjectObservable = serObject.asObservable()

    private val entryType: MapLikeType =
        mapper.typeFactory.constructMapLikeType(
            MapEntry::class.java,
            type.keyType ?: error("No key type for the map specified. Type: $type"),
            type.contentType ?: error("No content (value) type for map specified. Type: $type")
        )

    private var entriesCreated = 0

    companion object {
        const val ENTRY = "entry"
        const val ENTRY_KEY = "key"
        const val ENTRY_VALUE = "value"

        val keyCb = ENTRY_KEY.toCb()
        val valueCb = ENTRY_VALUE.toCb()

        data class MapEntry<K, V>(override val key: K?, override val value: V?) : Map.Entry<K?, V?> {}
    }

    /**
     * Check the if an entry with the given key exists
     */
    private fun contains(key: ClassBuilder): Boolean {
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

    private fun remove(key: ClassBuilder): Boolean {
        val entry = get(key) ?: return false
        serObject.remove(key)
        return serObject.remove(entry)
    }

    ////////////////////////////////////////
    //Variable sized parent class builder //
    ////////////////////////////////////////


    override fun createNewChild(): ComplexClassBuilder = create(TreeItem())

    override fun clear() = serObject.clear()

    //////////////////////////
    // parent class builder //
    //////////////////////////

    override fun createChild(
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

    override fun get(key: ClassBuilder): ClassBuilder? {
        return serObject.firstOrNull { it.key == key }
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

    override fun getChildren(): Map<ClassBuilder, ClassBuilder?> {
        return serObject.map { cb -> cb.key to cb }.toMap()
    }

    ///////////////////
    // Class Builder //
    ///////////////////

    override fun getPreviewValue(): String {
        return "Map<${type.keyType}, ${type.contentType}> of size ${serObject.size}"
    }

    override fun toString(): String {
        return "Map CB; key type=${type.keyType}, contained type=${type.contentType})"
    }
}
