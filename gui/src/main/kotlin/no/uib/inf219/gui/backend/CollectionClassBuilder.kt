package no.uib.inf219.gui.backend


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeView
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.primitive.IntClassBuilder
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
//@JsonSerialize(using = CollectionCBSerializer::class)
class CollectionClassBuilder<out T>(
    override val type: CollectionLikeType,
    override val name: String,
    override val parent: ClassBuilder<*>? = null,
    override val property: PropertyWriter? = null
) : ReferencableClassBuilder<Collection<T>>() {

    init {
        require(type.isTrueCollectionType) { "Given type $type is not a _true_ collection like type" }
    }

    companion object {
        private val sizeCb = 0.toCb("add location")
    }

    override val serObject: MutableList<ClassBuilder<*>> = ArrayList()

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
                        createClassBuilderFor(sizeCb)
                        controller.reloadView()
                        recompile()
                    }
                }
                this.add(tv)
            }
            this += find<PropertyEditor>(params = *arrayOf("controller" to con)).root
        }
    }

    private fun cbToInt(cb: ClassBuilder<*>?): Int? {
        return if (cb !is IntClassBuilder) null else cb.serObject
    }

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? {
        val index = cbToInt(key)
        if (index != null) {
            require(init == null || init.type == getChildType(key)) {
                "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
            }
            val elem = init ?: (getClassBuilder(type.contentType, serObject.size.toString()) ?: return null)
            serObject.add(index, elem)
            return elem
        } else
            return null
    }


    override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?) {
        val index: Int = cbToInt(key) ?: serObject.indexOf(element)
        if (index == -1) kotlin.error("Failed to find the element of ")

        val child = serObject[index]

        require(element == null || child == element) { "Given element is not equal to stored element at index $index" }

        if (child.reset()) {
            serObject.removeAt(index)
        }
    }

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>> {
        return serObject.mapIndexed { i, cb -> Pair(i.toCb("Element #$i"), cb) }.toMap()
    }

    override fun getPreviewValue(): String {
        return serObject.filter { !this.isParentOf(it) }.mapIndexed { i, cb -> "- $i: ${cb.getPreviewValue()}" }
            .joinToString("\n")
    }

    override fun getChildType(cb: ClassBuilder<*>): JavaType {
        return type.contentType
    }

    override fun getChildren(): List<ClassBuilder<*>> = serObject

    override fun isLeaf() = false

    override fun reset() = true

    override fun isImmutable() = false

    override fun toString(): String {
        return "Collection CB; value=${getPreviewValue()}, contained type=${type.contentType})"
    }

    @Suppress("DuplicatedCode")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CollectionClassBuilder<*>) return false

        if (type != other.type) return false
        if (parent != other.parent) return false
        if (name != other.name) return false
        if (property != other.property) return false
        if (serObject != other.serObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + name.hashCode()
        result = 31 * result + (property?.hashCode() ?: 0)
        return result
    }

}
