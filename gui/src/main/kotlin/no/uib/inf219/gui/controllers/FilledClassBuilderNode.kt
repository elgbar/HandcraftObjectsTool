package no.uib.inf219.gui.controllers

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder

/**
 * @author Elg
 */
data class FilledClassBuilderNode(
    override val key: ClassBuilder,
    override val cb: ClassBuilder,
    override val parent: ParentClassBuilder
) : ClassBuilderNode {

    override val item: TreeItem<ClassBuilderNode> = TreeItem(this)

    override fun ensurePresentClassBuilder(): FilledClassBuilderNode {
        return this
    }

    override fun resetClassBuilder(restoreDefault: Boolean): ClassBuilderNode? {
        return parent.resetChild(key, cb, restoreDefault)
    }

    override fun toString(): String {
        return "FilledClassBuilderNode(key=$key, parent=$parent)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilledClassBuilderNode

        if (key != other.key) return false
        if (cb != other.cb) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + cb.hashCode()
        result = 31 * result + parent.hashCode()
        return result
    }


}
