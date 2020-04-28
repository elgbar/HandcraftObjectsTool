@file:Suppress("LeakingThis")

package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.action
import tornadofx.item

/**
 * TODO move all meta information about children to Class Builder Node
 *
 * @author Elg
 */
abstract class ParentClassBuilder : ClassBuilder {

    init {
        require(key !== this) { "Cannot use a reference to self as key" }
    }

    /**
     *
     * @return all sub values this class can hold. The [Map.keys] must exactly return all valid keys.
     */
    @JsonIgnore
    abstract fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder?>

    /**
     * This is a subset of [getSubClassBuilders]
     *
     * @return all modifiable existing children
     */
    @JsonIgnore
    open fun getChildren(): List<ClassBuilder> {
        val list = ArrayList<ClassBuilder>()
        for ((k, v) in getSubClassBuilders()) {
            if (!k.isImmutable()) list += k
            if (v != null && !v.isImmutable()) list += v
        }
        return list
    }

    /**
     *
     * @param key The key to the property to create. All keys from [getSubClassBuilders] are guaranteed to work, others might work but it is up to the implementation to accept or reject keys
     * @param init The value to be placed at the given [key] property
     * @param item The item to be used when creating a new item, should be identical to the [init]'s item if it is not null
     *
     * @return A class builder for the given property type found at [key].
     *
     *
     * @throws IllegalArgumentException If the given [key] is not valid or [init] is invalid
     */
    abstract fun createChildClassBuilder(
        key: ClassBuilder,
        init: ClassBuilder? = null,
        item: TreeItem<ClassBuilderNode> = this.item.findChild(key)
    ): ClassBuilder?

    /**
     * Reset the given property for the [key] provided. If it has a default value this value will be restored otherwise it will be removed.
     *
     * @param key Specify which child class builder to reset
     * @param element The instance of the child to reset. Must be identical to the class builder found with [key] or be `null`. Essentially used as a check that the correct element is being reset.
     * @param restoreDefault If the default value (if none default is `null`) should be restored. If `false` the child found at [key] will be `null` after this method
     *
     * @return if the node was completely removed
     *
     * @throws IllegalArgumentException If child found with [key] does not match [element]. Will not be thrown if [element] is `null`
     */
    abstract fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder? = null,
        restoreDefault: Boolean = true
    )

    /**
     * Remove the current child at [key] (if any) and set the child at [key] to be [child]
     */
    open operator fun set(key: ClassBuilder, child: ClassBuilder) {
        checkChildValidity(key, child)

        resetChild(key, restoreDefault = false)

        createChildClassBuilder(key, child)
    }

    /**
     * @return The java type of of the given child
     */
    abstract fun getChildType(key: ClassBuilder): JavaType?

    /**
     * TODO make sure this can not return null
     *
     * @return The metadata of the child found at [key]
     *
     * @see ComplexClassBuilder.getChildPropertyMetadata
     */
    open fun getChildPropertyMetadata(key: ClassBuilder): ClassInformation.PropertyMetadata? = null

    /**
     * @return The child at the given location
     *
     * @throws IllegalArgumentException If the [key] is invalid
     */
    abstract fun getChild(key: ClassBuilder): ClassBuilder?

    fun getClassBuilder(
        type: JavaType,
        key: ClassBuilder,
        value: Any? = null,
        prop: ClassInformation.PropertyMetadata? = null,
        item: TreeItem<ClassBuilderNode> = TreeItem()
    ): ClassBuilder? {
        return ClassBuilder.createClassBuilder(type, key, this, value, prop, item)
    }

    protected fun checkChildValidity(key: ClassBuilder, child: ClassBuilder) {
        require(key == child.key) { "The key does not match the key of the child. key $key | child's key ${child.key}" }
        require(this === child.parent) { "Given child does not have this a parent" }

        require(key !== child) { "The key and child cannot be the same object" }

        require(getChildType(key)!!.isTypeOrSuperTypeOf(child.type.rawClass)) {
            "Wrong child type given. Expected type ${getChildType(key)} | child's type ${child.type}"
        }
        require(getChildPropertyMetadata(key) == child.property) {
            "Wrong property metadata given. Expected type ${getChildPropertyMetadata(key)} | child's type ${child.property}"
        }
    }

    /**
     * Some checks to make sure the given item, when updating internal structure, is correct
     */
    protected fun checkItemValidity(
        cb: ClassBuilder,
        expectedItem: TreeItem<ClassBuilderNode>,
        checkCB: Boolean = true
    ) {
        require(cb.item === expectedItem) { "Given item does not match init's item, expected $item init's item ${cb.item}" }
        require(!checkCB || expectedItem.value.cb === cb) { "Item's cbn class builder is not identical to given cb | item cbn cb: ${expectedItem.value.cb} | given cb $cb" }
        require(expectedItem.value.key == cb.key) { "Item's cbn  key does is not equal to given cb key | item cbn cb: ${expectedItem.value.key} | given cb ${cb.key}" }
        require(expectedItem.value.parent === cb.parent) { "Item's cbn parent does is not identical to given cb's parent | item cbn cb: ${expectedItem.value.parent} | given cb ${cb.parent}" }
    }

    /**
     * Create the context menu items that are displayed when right clicking a child node
     */
    fun createChildContextItems(
        key: ClassBuilder,
        menu: ContextMenu,
        controller: ObjectEditorController
    ) {
        val childMeta = getChildPropertyMetadata(key)
        val childCBN = item.findChild(key).value

        with(menu) {

            var prevSize = items.size

            //Display reset action if it does have a reset element
            if (childMeta?.hasValidDefaultInstance() == true) {
                item("Restore default").action {
                    childCBN.resetClassBuilder(controller.tree, true)
                }
            }

            //only allow deletion when there are something to delete
            if (childCBN.cb != null) {
                item("Delete").action {
                    childCBN.resetClassBuilder(controller.tree, false)
                }
            }

            if (items.size != prevSize) {
                //if we've added any new items add a separator
                items.add(prevSize, SeparatorMenuItem())
            }

            prevSize = items.size

            if (childCBN.cb?.createContextMenu(menu, controller) == true) {
                //add a separator before the child nodes elements
                items.add(prevSize, SeparatorMenuItem())
            }
        }
    }

    final override fun isLeaf(): Boolean = false


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ParentClassBuilder

        if (property != other.property) return false
        if (type != other.type) return false
        if (key.serObject != other.key.serObject) return false

        //parent can be a self reference
        if (parent !== other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = property?.hashCode() ?: 0
        result = 31 * result + type.hashCode()
        result = 31 * result + key.hashCode()

        // parent can be a self reference
        result = 31 * result + (if (parent !== this) parent.hashCode() else 0)
        return result
    }
}
