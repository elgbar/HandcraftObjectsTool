package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeView
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.NodeExplorerView
import no.uib.inf219.gui.view.PropertyEditor
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.*


/**
 *
 *
 * @author Elg
 */
class CollectionClassBuilder<out T>(
    override val type: CollectionLikeType,
    override val parent: ClassBuilder<*>? = null,
    override val name: String? = null,
    override val property: PropertyWriter? = null
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

    override fun toView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.splitpane {
            setDividerPositions(0.25)
            val con = ObjectEditorController(type, this@CollectionClassBuilder, controller)
            this += vbox {
                val nev = NodeExplorerView(con)
                val tv: TreeView<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>> = nev.root
                tv.showRootProperty().set(false)

                button("Add element") {
                    action {
                        val cb = getClassBuilder(type.contentType, collection.size.toString()) ?: return@action
                        collection.add(cb)
                        controller.reloadView()
                    }
                }
                this.add(tv)
            }
            this += PropertyEditor(con).root
        }
    }

    override fun createClassBuilderFor(property: String): ClassBuilder<*>? {
        return null
    }

    override fun isLeaf(): Boolean {
        return false
    }

    @Deprecated("Collection elements cannot be null", ReplaceWith("reset(property: String, element: ClassBuilder<*>)"))
    override fun reset(property: String): ClassBuilder<*>? {
        return super.reset(property)
    }

    override fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>? {
        require(element != null) { "Element cannot be null when removing from collection" }
        collection.remove(element)
        return null
    }

    override fun previewValue(): String {
        return collection.filter { !this.isParent(it) }.mapIndexed { i, cb -> "- $i: ${cb.previewValue()}" }
            .joinToString("\n")
    }

    override fun toString(): String {
        return "Collection CB; value=${previewValue()}, contained type=${type.contentType})"
    }
}
