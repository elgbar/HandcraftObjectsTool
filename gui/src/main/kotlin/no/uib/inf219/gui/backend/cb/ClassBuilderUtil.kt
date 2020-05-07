package no.uib.inf219.gui.backend.cb

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.Observable
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.backend.cb.simple.IntClassBuilder
import no.uib.inf219.gui.backend.cb.simple.StringClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView
import java.util.*

/**
 * Functions and values that should not be overwritten by the subclasses of ClassBuilder
 *
 * @author Elg
 */

/**
 * A class builder to be used by class builders where their parents are not of importance
 *
 * @see toCb
 * @see SimpleClassBuilder.parent
 */
val FAKE_ROOT = object : ParentClassBuilder() {
    override fun getChildren(): Map<ClassBuilder, ClassBuilder?> {
        error("Dummy parent")
    }

    override fun createChild(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder {
        error("Dummy parent")
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ) {
        error("Dummy parent")
    }

    override fun getChildType(key: ClassBuilder): JavaType? {
        error("Dummy parent")
    }

    override fun getChildPropertyMetadata(key: ClassBuilder) =
        ClassInformation.PropertyMetadata(key.getPreviewValue(), Any::class.type(), "", false, "", false)

    override fun get(key: ClassBuilder): ClassBuilder? {
        return if (key === this) this else null
    }

    override val parent = this
    override val key: ClassBuilder = this

    override val serObject: Any get() = error("Dummy parent ser obj")
    override val serObjectObservable: Observable get() = error("Dummy parent ser obj ob")
    override val type: JavaType get() = error("Dummy parent type")
    override val property: ClassInformation.PropertyMetadata get() = error("Dummy parent prop")
    override val item: TreeItem<ClassBuilderNode> = TreeItem()

    init {
        item.value = FilledClassBuilderNode(this, this, this, allowReference = false)
    }

    override fun createEditView(parent: EventTarget, controller: ObjectEditorController): Node {
        error("Dummy parent edit")
    }

    override fun getPreviewValue(): String {
        error("Dummy parent preview")
    }

    override fun isImmutable() = true
    override fun hashCode() = 0
    override fun equals(other: Any?) = this === other

}

/**
 * The [FilledClassBuilderNode] of this class builder, found found
 *
 * @see ClassBuilderNode
 */
val ClassBuilder.node: FilledClassBuilderNode
    get() = item.value as? FilledClassBuilderNode
        ?: error("The value of this tree item for class builder $this is not a FilledClassBuilderNode")

/**
 * The path to this object from it's root separated with ' | '
 */
val ClassBuilder.path: String
    get() {
        val list = LinkedList<ClassBuilder>()
        list.add(this)

        while (true) {
            val curr = list[0]
            val parent = curr.parent
            if (curr === parent) {
                break
            }

            list.add(0, parent)
        }
        list.removeAt(0)
        return list.joinToString(separator = " | ") { it.key.getPreviewValue() }
    }

/**
 * Convert this object to an instance of [ClassBuilder.type].
 */
fun ClassBuilder.toObject(): Any? {
    return ControlPanelView.mapper.convertValue(this, type)
}

/**
 * If this class builder is the legitimate child of the given parent. There may be multiple class builders between
 * this and the parent.
 */
fun ClassBuilder.isDescendantOf(parent: ClassBuilderNode): Boolean {
    return this.node.isDescendantOf(parent)
}

/**
 * If this class builder is the legitimate child of the given parent. There may be multiple class builders between
 * this and the parent.
 */
fun ClassBuilderNode.isDescendantOf(parent: ClassBuilder): Boolean {
    return this.isDescendantOf(parent.node)
}

/**
 * If this class builder is the legitimate child of the given parent. There may be multiple class builders between
 * this and the parent.
 */
fun ClassBuilder.isDescendantOf(parent: ClassBuilder): Boolean {
    return this.node.isDescendantOf(parent.node)
}

/**
 * If this class builder is the legitimate child of the given parent. There may be multiple class builders between
 * this and the parent.
 */
fun ClassBuilderNode.isDescendantOf(parentNode: ClassBuilderNode): Boolean {
    if (parentNode.cb !is ParentClassBuilder?) return false

    var currNode = this

    while (currNode.parent !== currNode) {
        try {
            val realCurrCb = currNode.parent[currNode.key]

            if (realCurrCb !== currNode.cb) {
                //it's not alive!
                return false
            } else if (realCurrCb === parentNode.cb) {
                //we've found the parent
                return true
            }
        } catch (ignored: IllegalArgumentException) {
            //get Child failed, the child is not alive!
            return false
        }
        currNode = currNode.parent.node
    }
    return currNode === parentNode
}

/**
 * Create [ClassBuilder]s from primitives
 *
 * @author Elg
 */
fun String.toCb(
    key: ClassBuilder? = null,
    parent: ParentClassBuilder? = null,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = true,
    item: TreeItem<ClassBuilderNode> = TreeItem()
): SimpleClassBuilder<String> {
    return StringClassBuilder(
        this,
        key,
        parent,
        property,
        immutable,
        item
    )
}

/**
 * Create [ClassBuilder]s from primitives
 *
 * @author Elg
 */
fun Int.toCb(
    key: ClassBuilder? = null,
    parent: ParentClassBuilder? = null,
    property: ClassInformation.PropertyMetadata? = null,
    immutable: Boolean = true,
    item: TreeItem<ClassBuilderNode> = TreeItem()
): SimpleClassBuilder<Int> {
    return IntClassBuilder(
        this,
        key,
        parent,
        property,
        immutable,
        item
    )
}
