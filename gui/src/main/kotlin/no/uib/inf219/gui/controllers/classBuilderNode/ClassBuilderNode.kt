package no.uib.inf219.gui.controllers.classBuilderNode

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.reload
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.loader.ClassInformation

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
    fun ensurePresentClassBuilder(tree: TreeView<ClassBuilderNode>): FilledClassBuilderNode

    fun getPropertyMeta(): ClassInformation.PropertyMetadata? {
        return parent.getChildPropertyMetadata(key)
    }

    /**
     * Reset the given class builder
     */
    fun resetClassBuilder(
        tree: TreeView<ClassBuilderNode>,
        restoreDefault: Boolean
    ) {
        parent.resetChild(key, cb, restoreDefault)
        tree.reload()
    }

    operator fun component1(): ClassBuilder {
        return key
    }

    operator fun component2(): ClassBuilder? {
        return cb
    }

    operator fun component3(): ParentClassBuilder {
        return parent
    }

    companion object {

        /**
         * Create the correct class builder node class instance from nullable cb
         */
        fun fromValues(key: ClassBuilder, cb: ClassBuilder?, parent: ParentClassBuilder): ClassBuilderNode {
            return cb?.node ?: EmptyClassBuilderNode(
                key,
                parent
            )
        }
    }
}
