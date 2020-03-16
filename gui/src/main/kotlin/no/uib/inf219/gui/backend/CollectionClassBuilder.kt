package no.uib.inf219.gui.backend


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeView
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.serializers.ParentClassBuilderSerializer
import no.uib.inf219.gui.backend.simple.IntClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.NodeExplorerView
import no.uib.inf219.gui.view.PropertyEditor
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.*


/**
 * @author Elg
 */
@JsonSerialize(using = ParentClassBuilderSerializer::class)
class CollectionClassBuilder<out T>(
    override val type: JavaType,
    override val key: ClassBuilder<*>? = null,
    override val parent: ClassBuilder<*>? = null,
    override val property: ClassInformation.PropertyMetadata? = null
) : ClassBuilder<Collection<T>> {

    init {
        require(type.isContainerType)
    }

    override val serObject = ArrayList<ClassBuilder<*>>()

    override val serObjectObservable = serObject.asObservable()

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
                        createClassBuilderFor(serObject.size.toCb())
                        controller.reloadView()
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
        return if (index != null) {
            require(init == null || init.type == getChildType(key)) {
                "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
            }
            val elem = init ?: (getClassBuilder(type.contentType, key)
                ?: kotlin.error("Failed to create class builder for $key"))
            serObject.add(index, elem)
            elem
        } else
            null
    }

    override fun getChild(key: ClassBuilder<*>): ClassBuilder<*>? {
        val index = cbToInt(key)
        require(index != null)
        return serObject[index]
    }


    //TODO Test
    override fun resetChild(
        key: ClassBuilder<*>,
        element: ClassBuilder<*>?,
        restoreDefault: Boolean
    ) {
        val index: Int = cbToInt(key) ?: serObject.indexOf(element)
        require(index in 0 until serObject.size) {
            "Given index is not within the range of the collection"
        }

        val child = serObject[index]

        require(element == null || child == element) { "Given element is not equal to stored element at index $index" }

        serObject.removeAt(index)
    }

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>> {
        return serObject.mapIndexed { i, cb -> Pair(i.toCb("Element #$i".toCb()), cb) }.toMap()
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
        if (key != other.key) return false
        if (property != other.property) return false
        if (serObject != other.serObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + key.hashCode()
        result = 31 * result + (property?.hashCode() ?: 0)
        return result
    }

}
