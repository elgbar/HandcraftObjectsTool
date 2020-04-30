@file:Suppress("LeakingThis")

package no.uib.inf219.gui.backend.cb.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
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
     * @return All class builder that this parent considers it's parent. All keys must be allowed to be used
     * with [createChild] and [resetChild]
     */
    @JsonIgnore
    abstract fun getChildren(): Map<ClassBuilder, ClassBuilder?>

    /**
     *
     * @param key The key to the property to create. All keys from [getChildren] are guaranteed to work, others
     * might work but it is up to the implementation to accept or reject keys
     * @param init The value to be placed at the given [key] property. It must be a valid child to be placed
     * here.
     * @param item The item to be used when creating a new item, should be identical to the [init]'s item if it
     * is not null
     *
     * @return A class builder for the given property type found at [key].
     *
     * @throws IllegalArgumentException If the given [key] is not valid or [init] is invalid
     */
    abstract fun createChild(
        key: ClassBuilder,
        init: ClassBuilder? = null,
        item: TreeItem<ClassBuilderNode> = this.item.findChild(key)
    ): ClassBuilder?

    /**
     * Reset the given property for the [key] provided. If it has a default value this value will be restored otherwise it will be removed.
     *
     * @param key Specify which child class builder to reset
     * @param element The instance of the child to reset. Must be identical to the class builder found with
     * [key] or be `null`. Essentially used as a check that the correct element is being reset.
     * @param restoreDefault If the default value (if none default is `null`) should be restored. If `false` the
     * child found at [key] will be `null` after this method
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
    open operator fun set(key: ClassBuilder, child: ClassBuilder?) {
        resetChild(key, restoreDefault = false)
        if (child != null) {
            checkChildValidity(key, child)
            checkItemValidity(child)

            createChild(key, child)
        }
    }

    /**
     * @return The child at the given location
     *
     * @throws IllegalArgumentException If the [key] is invalid
     */
    abstract operator fun get(key: ClassBuilder): ClassBuilder?

    /**
     * @return The java type of of the given child
     */
    abstract fun getChildType(key: ClassBuilder): JavaType?

    /**
     *
     * @return The metadata of the child found at [key]
     *
     */
    abstract fun getChildPropertyMetadata(key: ClassBuilder): ClassInformation.PropertyMetadata


    //////////////////////////
    // validation functions //
    //////////////////////////

    /**
     * Check if the given [other] class builder is an illegitimate child of this parent class builder.
     * An illegitimate child is a class builder that has all the necessary properties except for it's parent
     * to recognize it as a child.
     *
     * To put it in code `parent[other.key] === other` is `false` but `parent[other.key] == other` is `true` as long as `parent[other.key] != null`
     */
    protected fun isValidChild(other: ClassBuilder): Boolean {
        try {
            checkChildValidity(other.key, other)
            checkItemValidity(other)
            return true
        } catch (e: IllegalArgumentException) {
            return false
        }
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
        expectedItem: TreeItem<ClassBuilderNode> = item.findChild(cb.key),
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
            item("Restore default") {
                isDisable = childMeta?.hasValidDefaultInstance() != true
            }.action {
                childCBN.resetClassBuilder(controller.tree, true)
            }


            //only allow deletion when there are something to delete
            item("Delete") {
                isDisable = childCBN.cb == null
            }.action {
                childCBN.resetClassBuilder(controller.tree, false)
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
