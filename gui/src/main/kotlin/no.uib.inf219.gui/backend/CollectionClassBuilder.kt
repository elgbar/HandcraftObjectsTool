package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.NodeExplorerView
import no.uib.inf219.gui.view.PropertyEditor
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button

/**
 *
 * @param E The super type to create
 * @param T The collection to use with [E]
 *
 * @author Elg
 */
class CollectionClassBuilder<out T>(
    override val type: CollectionLikeType,
    override val parent: ClassBuilder<*>? = null,
    override val name: String? = null
) : ClassBuilder<Collection<T>> {

    init {
        require(type.isTrueCollectionType) { "Given type $type is not a _true_ collection like type" }
    }

    //TODO figure out how to get the correct type of collection to use
    private val collection: MutableCollection<ClassBuilder<*>> = ArrayList()

    override fun toObject(): Collection<T>? {
        try {
            @Suppress("UNCHECKED_CAST")
            return collection.map {
                try {
                    it.toObject() as T
                } catch (e: ClassCastException) {
                    throw IllegalArgumentException("Cannot convert ${it.type} to ${type.contentType}")
                }
            }
        } catch (e: java.lang.ClassCastException) {
            throw IllegalArgumentException("Cannot convert this collection class builder to $type")
        }
    }

    override fun getSubClassBuilders(): Map<String, ClassBuilder<*>> {
        return collection.mapIndexed { i, cb -> Pair(i.toString(), cb) }.toMap()
    }

    override fun toView(parent: EventTarget): Node {
        return parent.borderpane {
            val con = ObjectEditorController(type)
            left = NodeExplorerView(con).root
            center = PropertyEditor(con).root
            top = button("Add element") {
                action {
                    collection.add(getClassBuilder(type.contentType, collection.size.toString()))
                }
            }
        }
    }

    override fun createClassBuilderFor(property: String): ClassBuilder<*>? {
        return null
    }

    override fun reset(property: String): Boolean {
        collection.clear()
        return true
    }

    override fun previewValue(): String {
        return collection.filter { !this.isParent(it) }.mapIndexed { i, cb -> "- $i: ${cb.previewValue()}" }
            .joinToString("\n")
    }
}
