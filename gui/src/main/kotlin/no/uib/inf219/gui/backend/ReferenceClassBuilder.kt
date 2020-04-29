package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.isDescendantOf
import no.uib.inf219.extra.path
import no.uib.inf219.extra.textCb
import no.uib.inf219.gui.backend.events.ClassBuilderResetEvent
import no.uib.inf219.gui.backend.events.resetEvent
import no.uib.inf219.gui.backend.serializers.ParentClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.hbox
import tornadofx.onDoubleClick
import tornadofx.toProperty

/**
 * A reference to another class builder.
 *
 * @author Elg
 */
@JsonSerialize(using = ParentClassBuilderSerializer::class)
class ReferenceClassBuilder(
    private val refKey: ClassBuilder,
    private val refParent: ParentClassBuilder,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val item: TreeItem<ClassBuilderNode>
) : ClassBuilder {

    private var lastSeenSerObj = serObject

    override val serObject: ClassBuilder
        get() {
            val so = refParent.getChild(refKey)
                ?: error("Failed to find a serObject with the given reference parent and ref key. Cannot make a reference to a null class builder")
            if (so !== lastSeenSerObj) {
                lastSeenSerObj = so
            }
            return so
        }

    override val property: ClassInformation.PropertyMetadata? = parent.getChildPropertyMetadata(key)
    override val type: JavaType get() = serObject.type
    override val serObjectObservable = lastSeenSerObj.toProperty()

    private val event: (ClassBuilderResetEvent) -> Unit

    init {
        require(refKey != key || refParent !== parent) {
            "Direct cycle detected, the object we're serializing is this!"
        }

        event = { (cbn, restoreDefault) ->
            if (!restoreDefault && (cbn.parent === refParent && cbn.key === refKey) || refParent.isDescendantOf(cbn)) {
                onReset()
            }
        }
        resetEvent += event
    }

    private fun onReset() {
        //it was completely removed, this should be removed from the parent
        resetEvent -= event
        parent.resetChild(key, this@ReferenceClassBuilder, restoreDefault = true)
    }

    override fun createEditView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.hbox {
            alignment = Pos.CENTER

            onDoubleClick {
                controller.select(serObject)
            }

            textCb(serObject) {
                "This class builder is only a reference to object at ${this.path}.\nDouble click to edit the referenced class builder.\n\npreview: ${this.getPreviewValue()}"
            }
        }
    }

    override fun getPreviewValue() =
        "Ref to ${serObject.key.getPreviewValue()} property of ${serObject.parent.key.getPreviewValue()}"

    override fun isLeaf(): Boolean = true
    override fun isImmutable() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReferenceClassBuilder) return false

        //ser objects must be same object
        if (serObject !== other.serObject) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serObject.hashCode()
        result = 31 * result + parent.hashCode()
        return result
    }

    override fun toString(): String {
        return "Ref CB; ref child ${refKey.getPreviewValue()} of ${refParent.getPreviewValue()})"
    }
}
