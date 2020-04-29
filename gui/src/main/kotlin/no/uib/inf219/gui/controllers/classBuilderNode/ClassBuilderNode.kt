package no.uib.inf219.gui.controllers.classBuilderNode

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.reload
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.backend.events.ClassBuilderResetEvent
import no.uib.inf219.gui.backend.events.resetEvent
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
     * It it is allowed to create a reference ([no.uib.inf219.gui.backend.ReferenceClassBuilder]) to this node.
     *
     * @see no.uib.inf219.gui.backend.MapClassBuilder
     */
    val allowReference: Boolean

    /**
     * Ensure this node has a class builder present. If null is returned the user somehow failed to to create a child class builder
     */
    fun ensurePresentClassBuilder(tree: TreeView<ClassBuilderNode>): FilledClassBuilderNode?

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
        resetEvent(ClassBuilderResetEvent(this, restoreDefault))
        item.children.clear()
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
}
