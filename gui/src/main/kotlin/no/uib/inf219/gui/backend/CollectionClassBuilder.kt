package no.uib.inf219.gui.backend


import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.event.EventTarget
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import no.uib.inf219.extra.centeredText
import no.uib.inf219.extra.reload
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.backend.serializers.ParentClassBuilderSerializer
import no.uib.inf219.gui.backend.simple.IntClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.*
import kotlin.error


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

    override val serObject = ArrayList<ClassBuilder>().asObservable()
    override val serObjectObservable = serObject

    private fun createNewChild(controller: ObjectEditorController) {
        //make sure the key is mutable to support deletion of elements
        createChildClassBuilder(serObject.size.toCb(immutable = false), item = TreeItem())
        controller.tree.reload()
        item.isExpanded = true
    }

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Region {
        return parent.borderpane {
            center {
                centeredText("There are ${serObject.size} elements in this collection\n") {
                    button("Add new element").action {
                        createNewChild(controller)
                    }
                }
            }
        }
    }

    override fun onNodeClick(
        event: MouseEvent,
        controller: ObjectEditorController
    ) {
        if (event.clickCount == 2 && event.button == MouseButton.PRIMARY) {
            createNewChild(controller)
            event.consume()
        }
    }

    override fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean {
        with(menu) {
            item("Add new element").action { createNewChild(controller) }
            item("Clear").action {
                serObject.clear()
                item.children.clear()
                controller.tree.reload()
                item.isExpanded = false
            }
        }
        return true
    }

    private fun cbToInt(cb: ClassBuilder?): Int? {
        return if (cb !is IntClassBuilder) null else cb.serObject
    }

    override fun createChildClassBuilder(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder {
        val index = cbToInt(key)
            ?: error("Failed to create a new entry in a collection class builder at the given key is not an int")
        require(init == null || init.type == getChildType(key)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
        }
        val elem = init ?: (getClassBuilder(type.contentType, key, item = item)
            ?: error("Failed to create class builder for $key"))
        serObject.add(index, elem)
        this.item.children.add(elem.item)
        return elem
    }

    override fun getChild(key: ClassBuilder): ClassBuilder? {
        val index = cbToInt(key)
        require(index != null)
        return serObject[index]
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ) {
        val index: Int = cbToInt(key) ?: serObject.indexOf(element)
        require(index in 0 until serObject.size) {
            "Given index is not within the range of the collection"
        }

        val child = serObject[index]

        require(element == null || child == element) { "Given element is not equal to stored element at index $index. given = $element, stored = $child" }

        serObject.remove(child)
        item.children.remove(child.item)

        //decrease the index (key) of all element after the deleted element
        for (builder in serObject.drop(index)) {
            //sanity check, just in case and for a nicer error message
            require(builder.key is IntClassBuilder) { "Key of collection element is not int! it is ${builder.key}" }

            (builder.key as IntClassBuilder).serObject--
        }
    }

    override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder> {
        return serObject.mapIndexed { i, cb -> i.toCb() to cb }.toMap()
    }

    override fun getPreviewValue() = "Collection of ${type.rawClass.simpleName}"
    

    override fun getChildType(key: ClassBuilder): JavaType {
        return type.contentType
    }

    override fun getChildPropertyMetadata(key: ClassBuilder): ClassInformation.PropertyMetadata? {
        return ClassInformation.PropertyMetadata(
            key.getPreviewValue(),
            type.contentType,
            "",
            false,
            "An entry in a collection",
            true
        )
    }

    override fun getChildren(): List<ClassBuilder> = serObject

    override fun isImmutable() = false

    override fun toString(): String {
        return "Collection CB; containing=${type.contentType}, value=${serObject}"
    }
}
