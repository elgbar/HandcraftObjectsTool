package no.uib.inf219.gui.backend.cb.parents


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.VariableSizedParentClassBuilder
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.serializers.ParentClassBuilderSerializer
import no.uib.inf219.gui.backend.cb.simple.IntClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.asObservable


/**
 * @author Elg
 */
@JsonSerialize(using = ParentClassBuilderSerializer::class)
class CollectionClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata? = null,
    override val item: TreeItem<ClassBuilderNode>
) : VariableSizedParentClassBuilder() {

    init {
        require(type.isContainerType)
    }

    override val serObject = ArrayList<ClassBuilder>().asObservable()
    override val serObjectObservable = serObject

    ////////////////////////////////////////
    //Variable sized parent class builder //
    ////////////////////////////////////////

    override fun createNewChild(controller: ObjectEditorController): ClassBuilder? {
        //make sure the key is mutable to support deletion of elements
        return createChild(serObject.size.toCb(immutable = false), item = TreeItem())
    }

    override fun clear() = serObject.clear()

    //////////////////////////
    // parent class builder //
    //////////////////////////

    private fun cbToInt(cb: ClassBuilder?): Int? {
        return if (cb !is IntClassBuilder) null else cb.serObject
    }

    override fun createChild(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder? {
        val index = cbToInt(key)
            ?: error("Failed to create a new entry in a collection class builder at the given key is not an int")
        require(init == null || init.type == getChildType(key)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
        }

        val elem = init ?: createClassBuilder(type.contentType, key, this, item = item)
        ?: return null

        checkChildValidity(key, elem)
        checkItemValidity(elem, item)

        serObject.add(index, elem)
        this.item.children.add(index, elem.item)
        return elem
    }

    override fun get(key: ClassBuilder): ClassBuilder {
        val index = cbToInt(key) ?: error("Given index cannot be null")
        try {
            return serObject[index]
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun set(key: ClassBuilder, child: ClassBuilder?) {
        val index: Int = cbToInt(key) ?: serObject.indexOf(child)
        require(index in 0 until serObject.size) {
            "Given index is not within the range of the collection"
        }
        if (child == null) {
            resetChild(key)
            return
        } else if (index == serObject.size) {
            //we're adding a new object use the normal method
            createChild(key, child)
            return
        }
        checkChildValidity(key, child)
        checkItemValidity(child)

        serObject[index] = child
        item.children[index] = child.item
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ) {
        val index: Int = cbToInt(key) ?: serObject.indexOf(element)
        require(index in 0 until serObject.size) {
            "Given index is not within the range of the collection"
        }

        val child = serObject[index]

        require(element == null || child === element) { "Given element is not equal to stored element at index $index. given = $element, stored = $child" }

        serObject.remove(child)
        item.children.remove(child.item)

        //decrease the index (key) of all element after the deleted element
        for (builder in serObject.drop(index)) {
            //sanity check, just in case and for a nicer error message
            require(builder.key is IntClassBuilder) { "Key of collection element is not int! it is ${builder.key}" }

            (builder.key as IntClassBuilder).serObject--
        }
    }

    override fun getChildren(): Map<ClassBuilder, ClassBuilder> {
        return serObject.mapIndexed { i, cb -> i.toCb() to cb }.toMap()
    }

    override fun getPreviewValue() = "${type.rawClass.simpleName} of ${type.contentType}"

    override fun getChildType(key: ClassBuilder): JavaType = type.contentType

    override fun getChildPropertyMetadata(key: ClassBuilder) = ClassInformation.PropertyMetadata(
        key.getPreviewValue(),
        type.contentType,
        "",
        false,
        "An entry in a collection",
        true
    )

    override fun toString(): String {
        return "Collection CB; containing=${type.contentType}, value=${serObject}"
    }
}
