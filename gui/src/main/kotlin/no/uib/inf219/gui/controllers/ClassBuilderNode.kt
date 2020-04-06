package no.uib.inf219.gui.controllers

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder

/**
 * Represents a node in the tree of [no.uib.inf219.gui.view.NodeExplorerView]
 *
 * @author Elg
 */
interface ClassBuilderNode {

    /**
     * Key of this class builder, doubles up as the text displayed in the tree view
     */
    val key: ClassBuilder

    /**
     * The class builder this node references.
     */
    val cb: ClassBuilder?

    /**
     * The parent of this node
     */
    val parent: ParentClassBuilder

    val item: TreeItem<ClassBuilderNode>

    /**
     * Ensure this node has a class builder present. Calling this with a [FilledClassBuilderNode] will return `this`
     */
    fun ensurePresentClassBuilder(): FilledClassBuilderNode

    /**
     * Reset the given class builder
     */
    fun resetClassBuilder(restoreDefault: Boolean): ClassBuilderNode?

    fun asEmpty(): EmptyClassBuilderNode {
        return EmptyClassBuilderNode(key, parent)
    }

    companion object {

        /**
         * Create the correct class builder node class instance from nullable cb
         */
        fun fromValues(key: ClassBuilder, cb: ClassBuilder?, parent: ParentClassBuilder): ClassBuilderNode {
            return cb?.node ?: EmptyClassBuilderNode(key, parent)
        }
    }
}
