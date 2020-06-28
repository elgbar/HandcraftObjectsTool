/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("LeakingThis")

package no.uib.inf219.gui.backend.cb.api

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import javafx.scene.control.ContextMenu
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.findChild
import no.uib.inf219.extra.isTypeOrSuperTypeOfPrimAsObj
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.action
import tornadofx.item

/**
 * TODO move all meta information about children to Class Builder Node
 *
 * The parent class builder is an abstract implementation of the class builder. It is the superclass of all class builders
 * that have child properties of their own.
 *
 * @author Elg
 *
 * @see no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
 * @see no.uib.inf219.gui.backend.cb.api.VariableSizedParentClassBuilder
 */
abstract class ParentClassBuilder : ClassBuilder {

    init {
        require(key !== this) { "Cannot use a reference to self as key" }
    }

    /**
     * @return All child properties that this acknowledge as its own children. There might be class builders that have
     * this as its [parent], but unless listed in the [Map.values] set they are considered illegitimate.
     *
     * All keys must be allowed to be used with [createChild] and [resetChild]
     *
     * @see no.uib.inf219.gui.backend.cb.isDescendantOf
     * @see no.uib.inf219.gui.backend.cb.isDescendantOf
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
     * @param key The key to the property to remove. All keys from [getChildren] are guaranteed to work, others
     * might work but it is up to the implementation to accept or reject keys
     * @param element The instance of the child to reset. Must be identical to the class builder found with
     * [key] or be `null`. Essentially used as a check that the correct element is being reset.
     * @param restoreDefault If the default value (if none default is `null`) should be restored. If `false` the
     * child found at [key] will be `null` after this method
     *
     * @throws IllegalArgumentException If the given [key] is not valid
     * @throws IllegalArgumentException If child found with [key] does not match [element]. Will not be thrown if [element] is `null`
     *
     * @see ClassInformation.PropertyMetadata.getDefaultInstance
     */
    abstract fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder? = null,
        restoreDefault: Boolean = true
    )

    /**
     * Replace the current child at [key] (if any) and set the child at [key] to be [child]. The default instance first removed (without restoring) then creates with [child] as initial element.
     *
     * @param key The key to the property to replace. All keys from [getChildren] are guaranteed to work, others
     * might work but it is up to the implementation to accept or reject keys
     * @param child The value to place the current property with
     *
     * @throws IllegalArgumentException If the given [key] is not valid
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
     * @param key The key to the property to get. All keys from [getChildren] are guaranteed to work, others
     * might work but it is up to the implementation to accept or reject keys
     *
     * @return The child property with the given key
     *
     * @throws IllegalArgumentException If the given [key] is not valid
     */
    abstract operator fun get(key: ClassBuilder): ClassBuilder?

    /**
     * @param key The key to the property to get. All keys from [getChildren] are guaranteed to work, others
     * might work but it is up to the implementation to accept or reject keys
     *
     * @return The java type of of the given child
     *
     * @throws IllegalArgumentException If the given [key] is not valid
     */
    abstract fun getChildType(key: ClassBuilder): JavaType?

    /**
     * @param key The key to the property to get. All keys from [getChildren] are guaranteed to work, others
     * might work but it is up to the implementation to accept or reject keys
     *
     * @return The metadata of the child found at [key]
     * @throws IllegalArgumentException If the given [key] is not valid
     */
    abstract fun getChildPropertyMetadata(key: ClassBuilder): ClassInformation.PropertyMetadata

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
                isDisable = childMeta.hasValidDefaultInstance() != true
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

    //parents can never be leaves
    final override fun isLeaf(): Boolean = false

    ////////////////////////
    // validation methods //
    ////////////////////////

    /**
     * Check if the given [other] class builder is an illegitimate child of this parent class builder.
     * An illegitimate child is a class builder that has all the necessary properties except for it's parent
     * to recognize it as a child.
     *
     * To put it in code `parent[other.key] === other` is `false` but `parent[other.key] == other` is `true` as long as `parent[other.key] != null`
     */
    protected fun isValidChild(other: ClassBuilder): Boolean {
        return try {
            checkChildValidity(other.key, other)
            checkItemValidity(other)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    /**
     * Check that the given child is a good match for a property at [key]
     */
    protected fun checkChildValidity(key: ClassBuilder, child: ClassBuilder) {
        require(key == child.key) { "The key does not match the key of the child. key $key | child's key ${child.key}" }
        require(this === child.parent) { "Given child does not have this a parent" }

        require(key !== child) { "The key and child cannot be the same object" }

        require(getChildType(key)!!.isTypeOrSuperTypeOfPrimAsObj(child.type)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${child.type}"
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
