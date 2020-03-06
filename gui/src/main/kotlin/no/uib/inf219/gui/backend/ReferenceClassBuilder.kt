package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import no.uib.inf219.gui.backend.serializers.ReferenceClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.hbox
import tornadofx.onDoubleClick
import tornadofx.text

/**
 * A reference to another class builder.
 *
 * @author Elg
 */
@JsonSerialize(using = ReferenceClassBuilderSerializer::class)
class ReferenceClassBuilder(
    /**
     * The class builder this class builder is referencing
     */
    override val serObject: ClassBuilder<*>,
    override val parent: ClassBuilder<*>?
) : ReferencableClassBuilder<Any>() {

    override val type: JavaType = serObject.type
    override val name: String = "ref " + serObject.name
    override val property: PropertyWriter? = serObject.property

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.hbox {
            alignment = Pos.CENTER

            onDoubleClick {
                controller.select(serObject.name, serObject)
            }
            text("This class builder is only a reference to ${serObject.getPreviewValue()}. Double click to edit the referenced class builder.")
        }
    }

    override fun getPreviewValue() = "Ref to " + serObject.getPreviewValue()

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> = emptyMap()

    override fun isLeaf(): Boolean = true

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? = null

    override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?) {}

    override fun getChildType(cb: ClassBuilder<*>): JavaType? = null

    override fun isImmutable() = true

    override fun reset() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReferenceClassBuilder) return false

        if (serObject != other.serObject) return false
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
