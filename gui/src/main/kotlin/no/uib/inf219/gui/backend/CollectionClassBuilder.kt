package no.uib.inf219.gui.backend


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.serializers.ParentClassBuilderSerializer
import no.uib.inf219.gui.backend.simple.IntClassBuilder
import no.uib.inf219.gui.controllers.ClassBuilderNode
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.action
import tornadofx.asObservable
import tornadofx.borderpane
import tornadofx.button


/**
 * @author Elg
 */
@JsonSerialize(using = ParentClassBuilderSerializer::class)
class CollectionClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata? = null,
    override val item: TreeItem<ClassBuilderNode>
) : ParentClassBuilder() {

    init {
        require(type.isContainerType)
    }

    override val serObject = ArrayList<ClassBuilder>()

    override val serObjectObservable = serObject.asObservable()

    override fun toView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.borderpane {
            center = button("Add element") {
                action {
                    val newCb = createClassBuilderFor(serObject.size.toCb())
                    item.children.add(newCb.item)

                    controller.tree.refresh()
                }
            }
        }
    }

    private fun cbToInt(cb: ClassBuilder?): Int? {
        return if (cb !is IntClassBuilder) null else cb.serObject
    }

    override fun createClassBuilderFor(key: ClassBuilder, init: ClassBuilder?): ClassBuilder {
        val index = cbToInt(key)
            ?: error("Failed to create a new entry in a collection class builder at the given key is not an int")
        require(init == null || init.type == getChildType(key)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
        }
        val elem = init ?: (getClassBuilder(type.contentType, key)
            ?: error("Failed to create class builder for $key"))
        serObject.add(index, elem)
        return elem
    }

    override fun getChild(key: ClassBuilder): ClassBuilder? {
        val index = cbToInt(key)
        require(index != null)
        return serObject[index]
    }

    //TODO Test
    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ): ClassBuilderNode? {
        val index: Int = cbToInt(key) ?: serObject.indexOf(element)
        require(index in 0 until serObject.size) {
            "Given index is not within the range of the collection"
        }

        val child = serObject[index]

        require(element == null || child == element) { "Given element is not equal to stored element at index $index. given = $element, stored = $child" }
        val removed = serObject.removeAt(index)
        require(element == null || element == removed) { "Element removed was not equal to child element. given = $element, removed = $removed" }
        return null
    }

    override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder> {
        return serObject.mapIndexed { i, cb -> Pair<ClassBuilder, ClassBuilder>(i.toCb(), cb) }.toMap()
    }

    override fun getPreviewValue(): String {
        return serObject.filter { !this.isParentOf(it) }.mapIndexed { i, cb -> "- $i: ${cb.getPreviewValue()}" }
            .joinToString("\n")
    }

    override fun getChildType(cb: ClassBuilder): JavaType {
        return type.contentType
    }

    override fun getChildren(): List<ClassBuilder> = serObject

    override fun isImmutable() = false

    override fun toString(): String {
        return "Collection CB; value=${getPreviewValue()}, contained type=${type.contentType})"
    }
}
