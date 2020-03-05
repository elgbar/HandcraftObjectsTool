package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import no.uib.inf219.gui.backend.serializers.ClassBuilderCompiler
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.hbox
import tornadofx.onDoubleClick
import tornadofx.text

/**
 * A reference to another class builder.
 *
 * @author Elg
 */
class ReferenceClassBuilder(
    /**
     * The class builder this class builder is referencing
     */
    override val serializationObject: ClassBuilder<*>,
    override val parent: ClassBuilder<*>?
) : ReferencableClassBuilder<Any>() {

    override val type: JavaType = serializationObject.type
    override val name: String = "ref " + serializationObject.name
    override val property: PropertyWriter? = serializationObject.property

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.hbox {
            alignment = Pos.CENTER

            onDoubleClick {
                controller.select(
                    serializationObject.name,
                    serializationObject
                )
            }
            text("This class builder is only a reference to ${serializationObject.getPreviewValue()}. Double click to edit the referenced class builder.")
        }
    }

    override fun compile(cbs: ClassBuilderCompiler): Any {

        val pair = cbs.getValue(serializationObject)
        return if (pair == null) {
            val compiled = cbs.compile(serializationObject)
            //after
            cbs.setValue(this, compiled)
            compiled
        } else {
            //this has already been seen so we return the uuid to be resolved later
            pair.first
        }
    }

    override fun link(cbs: ClassBuilderCompiler, obj: Any) {
//        if (obj is UUID) {
//            //If the object is an UUID it might be a reference to another object.
//            // or it might just be a UUID used in the created object
//            return cbs.resolveReference(obj)
//        }
//        return obj
    }

    override fun getPreviewValue() = "Ref to " + serializationObject.getPreviewValue()

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

        if (serializationObject != other.serializationObject) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serializationObject.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Ref CB; ref=$serializationObject, clazz=$type)"
    }
}
