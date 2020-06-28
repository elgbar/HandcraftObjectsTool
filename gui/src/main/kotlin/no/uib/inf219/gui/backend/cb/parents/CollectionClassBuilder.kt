/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui.backend.cb.parents


import com.fasterxml.jackson.databind.JavaType
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.backend.cb.api.VariableSizedParentClassBuilder
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.simple.IntClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.EmptyClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.action
import tornadofx.asObservable
import tornadofx.item


/**
 * The class builder variant for [Collection]s and [Array]s.
 *
 * @author Elg
 */
class CollectionClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata? = null,
    override val item: TreeItem<ClassBuilderNode>
) : VariableSizedParentClassBuilder() {

    init {
        require(type.isContainerType)
    }

    override val serObject = ArrayList<ClassBuilder?>()
    override val serObjectObservable = serObject.asObservable()

    ////////////////////////////////////////
    //Variable sized parent class builder //
    ////////////////////////////////////////

    override fun createNewChild(): ClassBuilder? {
        //make sure the key is mutable to support deletion of elements
        return createChild(createChildKey(serObject.size), item = TreeItem())
    }

    fun createNullChild() {
        val childKey = createChildKey(serObject.size)
        serObject.add(null) //this call must be after creating the key
        this.item.children.add(EmptyClassBuilderNode(childKey, this).item)
    }

    override fun clear() = serObject.clear()

    //////////////////////////
    // parent class builder //
    //////////////////////////

    private fun cbToInt(cb: ClassBuilder?): Int? {
        return if (cb !is IntClassBuilder) null else cb.serObject
    }

    override fun createChild(key: ClassBuilder, init: ClassBuilder?, item: TreeItem<ClassBuilderNode>): ClassBuilder? {
        val index = cbToInt(key)
            ?: error("Failed to create a new entry in a collection class builder at the given key is not an int")

        checkCollectionElem(index, init)
        val elem = init ?: createClassBuilder(type.contentType, key, this, item = item)
        ?: return null

        checkChildValidity(key, elem)
        checkItemValidity(elem, item)
        if (this.item.children.contains(item)) {
            check(this.item.children[index] == item) {
                """parent items contain the child item, but at the wrong index! 
                   Expected index of child $index, it was found at ${this.item.children.indexOf(item)}
                """.trimIndent()
            }
            serObject[index] = elem
        } else {
            serObject.add(index, elem)
            this.item.children.add(index, item)
        }
        return elem
    }

    override fun get(key: ClassBuilder): ClassBuilder? {
        val index = cbToInt(key) ?: error("Given index cannot be null")
        try {
            return serObject[index]
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalArgumentException(e)
        }
    }

    override fun set(key: ClassBuilder, child: ClassBuilder?) {
        val index: Int = cbToInt(key) ?: serObject.indexOf(child)
        if (child == null) {
            resetChild(key)
            return
        } else if (index == serObject.size) {
            //we're adding a new object use the normal method
            createChild(key, child, child.item)
            return
        }

        checkCollectionElem(index, child)
        checkChildValidity(key, child)
        checkItemValidity(child)

        serObject[index] = child
        item.children[index] = child.item
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

        require(element == null || child === element) { "Given element is not equal to stored element at index $index. given = $element, stored = $child" }

        serObject.remove(child)
        item.children.remove(child?.item)

        //make sure every index is correct
        for ((i, builder) in item.children.withIndex()) {
            //sanity check, just in case and for a nicer error message
            require(builder.value.key is IntClassBuilder) { "Key of collection element is not int! it is ${builder.value.key}" }

            (builder.value.key as IntClassBuilder).serObject = i
        }
    }

    /**
     * Checks that must be passed to let a child be created in this collection
     */
    private fun checkCollectionElem(index: Int, child: ClassBuilder?) {
        require(index in 0..serObject.size) {
            "Given index is not within the range of the collection"
        }
        require(child == null || !child.key.isImmutable()) { "Keys in a collection class builder must be immutable" }
    }

    override fun getChildren(): Map<ClassBuilder, ClassBuilder?> {
        return serObject.mapIndexed { i, cb -> i.toCb() to cb }.toMap()
    }

    override fun getPreviewValue() = "${type.rawClass.simpleName} of ${type.contentType}"

    override fun getChildType(key: ClassBuilder): JavaType = type.contentType

    override fun getChildPropertyMetadata(key: ClassBuilder) = ClassInformation.PropertyMetadata(
        key.getPreviewValue(),
        type.contentType,
        "",
        false,
        "An entry in a collection",
        true
    )

    override fun toString(): String {
        return "Collection CB; containing=${type.contentType}, value=${serObject}"
    }

    override fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean {
        super.createContextMenu(menu, controller)
        with(menu) {
            item("Add new null entry").action {
                expand()
                createNullChild()
            }
        }
        return true
    }

    companion object {
        fun createChildKey(index: Int): SimpleClassBuilder<Int> {
            return index.toCb(immutable = false)
        }
    }
}
