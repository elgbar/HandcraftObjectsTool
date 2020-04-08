package no.uib.inf219.gui.controllers.classBuilderNode

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.reload
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder

/**
 * A class builder node where the class builder is always null aka empty
 */
class EmptyClassBuilderNode(
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val item: TreeItem<ClassBuilderNode> = TreeItem()
) : ClassBuilderNode {

    override val cb: ClassBuilder?

    init {
        this.cb = null
        item.value = this
    }


    override fun ensurePresentClassBuilder(tree: TreeView<ClassBuilderNode>): FilledClassBuilderNode {
        val cb = parent.createChildClassBuilder(key, item = item)
        tree.reload()
        return cb.node
    }

    override fun toString(): String {
        return "EmptyClassBuilderNode(key=$key, parent=$parent)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EmptyClassBuilderNode

        if (key != other.key) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + parent.hashCode()
        return result
    }
}
