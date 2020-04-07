@file:Suppress("LeakingThis")

package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation

/**
 * TODO move all the overwritten methods here from ClassBuilder
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
    ): ClassBuilder

    /**
     * Reset the given property for the [key] provided. If it has a default value this value will be restored otherwise it will be removed.
     *
     * @param key Specify which child class builder to reset
     * @param element The instance of the child to reset. Must be identical to the class builder found with [key] or be `null`
     * @param restoreDefault If the default value (if none default is `null`) should be restored. If `false` the child found at [key] will be `null` after this method
     *
     * @return The node to show. If null is returned the node will be removed
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
    operator fun set(key: ClassBuilder, child: ClassBuilder) {
        require(child.key == key) { "Key does not match" }
        require(child.parent == this) { "Given child does not have this a parent" }
        resetChild(key, restoreDefault = false)
        createChildClassBuilder(key, child)
    }

    /**
     * @return The java type of of the given child
     */
    abstract fun getChildType(key: ClassBuilder): JavaType?

    /**
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

    /**
     * @return If this is a forefather of the given [ClassBuilder]. Will return `false` if `this` is equal to [to]
     */
    fun isParentOf(to: ClassBuilder?): Boolean {
        if (to == null) return false
        return when (to.parent) {
            to -> false //to's parent is it self (recursive assignment, denotes root cb)
            this -> true
            else -> this.isParentOf(to.parent)
        }
    }

//    @JsonIgnore
//    open fun getTreeItems(): List<TreeItem<ClassBuilderNode>> = getSubClassBuilders().map { item.findChild(it.key) }

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
