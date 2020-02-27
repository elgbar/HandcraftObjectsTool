package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.controllers.ObjectEditorController

/**
 * A reference to another class builder.
 *
 * @author Elg
 */
class ReferenceClassBuilder(
    val referenced: ClassBuilder<*>,
    override val parent: ClassBuilder<*>?
) : ClassBuilder<Any> {

    override val type: JavaType = referenced.type
    override val name: String = "ref " + referenced.name
    override val property: PropertyWriter? = referenced.property

    override fun toTree(): JsonNode {
        return referenced.toTree()
    }

    override fun toObject(): Any? {
        return referenced.toObject()
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>?> {
        return referenced.getSubClassBuilders()
    }

    override fun isLeaf(): Boolean {
        return referenced.isLeaf()
    }

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {
        return referenced.toView(parent, controller)
    }

    override fun createClassBuilderFor(property: String): ClassBuilder<*>? {
        return referenced.createClassBuilderFor(property)
    }

    override fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>? {
        return referenced.reset(property, element)
    }

    override fun previewValue(): String {
        return referenced.previewValue()
    }

    override fun recompile() {
        referenced.recompile()
        parent?.recompile()
    }
}
