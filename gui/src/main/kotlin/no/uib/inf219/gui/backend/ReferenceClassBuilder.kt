package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import no.uib.inf219.extra.onChange
import no.uib.inf219.extra.textCb
import no.uib.inf219.gui.backend.serializers.ReferenceClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.hbox
import tornadofx.onDoubleClick
import tornadofx.toProperty

/**
 * A reference to another class builder.
 *
 * @author Elg
 */
@JsonSerialize(using = ReferenceClassBuilderSerializer::class)
class ReferenceClassBuilder(
    private val refKey: ClassBuilder<*>,
    private val refParent: ClassBuilder<*>,
    override val key: ClassBuilder<*>,
    override val parent: ClassBuilder<*>
) : ClassBuilder<Any> {

    override var serObject: ClassBuilder<*> = refParent.getChild(refKey)
        ?: error("Failed to find a serObject with the given reference parent and ref key. Cannot make a reference to a null class builder")
        get() {
//            val newRef = refParent.getChild(refKey)
//            if (newRef !== field && newRef != null) {
//                //we have a new object to reference
//                serObject = newRef
//            }
            return field
        }
        private set

    override val type: JavaType = serObject.type
    override val property: PropertyWriter? = serObject.property
    override val serObjectObservable = serObject.toProperty()

    init {
        //TODO test if reference get reconnected if the referencing object is reset
        // test: If ref is nulled out this should also be nulled out

        require(serObject !== this) {
            "Direct cycle detected, the object we're serializing is this!"
        }

        require(serObject !is ReferenceClassBuilder || !parent.isLeaf()) {
            //TODO find out if reference chaining should be allowed. If not update this message with why.
            "Chain of cb references is not supported as of now"
        }

//            //run this later, the parent need to have time to assign this as a child to it self
//            require(parent.getChild(key) === this) { "Parent with given key does not give this class builder" }


        //TODO what if we reference root (ie null)?
        refParent.serObjectObservable.onChange {
            val newRef = refParent.getChild(refKey)
            if (newRef !== serObject) {
                if (newRef == null) {
                    //it was completely removed, this should be removed from out parent
                    // but should this be restored or nulled out? The safest is probably to restore it
                    parent.resetChild(key, restoreDefault = true)
                } else {
                    serObject = newRef
                }
            }
        }
    }

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.hbox {
            alignment = Pos.CENTER

            onDoubleClick {
                controller.select(serObject)
            }

            textCb(serObject) {
                "This class builder is only a reference to ${getPreviewValue()}. Double click to edit the referenced class builder."
            }
        }
    }

    override fun getPreviewValue() = "Ref to " + serObject.getPreviewValue()

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> = emptyMap()

    override fun isLeaf(): Boolean = true

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? =
        serObject.createClassBuilderFor(key, init)

    override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?, restoreDefault: Boolean) =
        serObject.resetChild(key, element, restoreDefault)

    override fun getChildType(cb: ClassBuilder<*>): JavaType? = serObject.getChildType(cb)

    override fun getChild(key: ClassBuilder<*>): ClassBuilder<*>? {
        return serObject.getChild(key)
    }

    override fun isImmutable() = serObject.isImmutable()

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
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Ref CB; ref=$serObject)"
    }
}
