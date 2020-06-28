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

package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.isTypeOrSuperTypeOfPrimAsObj
import no.uib.inf219.extra.selectedItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.displayReferenceWarning
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation.PropertyMetadata
import no.uib.inf219.gui.view.ControlPanelView
import tornadofx.text
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * @author Elg
 */
class ObjectEditorController(
    rootType: JavaType,
    obj: Any? = null
) {
    lateinit var tree: TreeView<ClassBuilderNode>

    /**
     * The fake root
     *
     * @see RootDelegator
     */
    private val fakeRoot by RootDelegator(rootType, obj)
    val root: ClassBuilder get() = fakeRoot.serObject as ClassBuilder

    fun select(cb: ClassBuilder) {
        tree.selectionModel.select(cb.item)
    }

    /**
     * @return if the selected value is non-null
     */
    fun createSelected(): ClassBuilderNode? {
        with(tree) {
            val item = selectedItem ?: return null
            if (item == root) return null

            val newCbn = item.value.ensurePresentClassBuilder(this) ?: return null
            item.value = newCbn
            item.isExpanded = true

            return newCbn
        }
    }

    fun deleteSelected() {
        with(tree) {
            val item = selectedItem ?: return
            if (item == root) return
            item.value.resetClassBuilder(this, false)
        }
    }

    companion object {
        val fakeRootKey = "root".toCb()
    }

    /**
     * Delegator that handles resetting of our real root. It acts as any other class builder is expected to behave: It is a parent class builder with one child with key [fakeRootKey] and the value [serObject]. The real root can never be `null` so when asked to reset it ignores `restoreDefault` and behaves as if it is always `true`. The default value of the root is what is returned when calling [ClassBuilder.createClassBuilder] with type [realRootType].
     */
    private class RootDelegator(
        private val realRootType: JavaType,
        private val rootObj: Any?
    ) : ParentClassBuilder(),
        ReadOnlyProperty<Any?, ClassBuilder> {


        init {
            require(rootObj == null || realRootType.isTypeOrSuperTypeOfPrimAsObj(rootObj::class.type())) {
                "Mismatch between type and object given. Expected $realRootType, got ${rootObj?.javaClass?.type()}"
            }
        }

        /** Key to the real root */
        val realRootKey = realRootType.rawClass.simpleName.toCb()
        override val parent = this

        /**
         * (re)create the root class builder
         */
        private fun createRealRoot(): ClassBuilder {
            val rootPropMeta = PropertyMetadata(
                realRootKey.serObject,
                realRootType,
                "",
                true,
                "The object that is currently being created",
                false
            )
            val type = rootObj?.javaClass?.type() ?: realRootType
            val cb = createClassBuilder(type, realRootKey, this, rootObj, rootPropMeta, TreeItem())
                ?: error("failed to create a root class builder")

            if (cb is ParentClassBuilder && rootObj != null) {
                val rootTree: JsonNode = ControlPanelView.mapper.valueToTree(rootObj)
                displayReferenceWarning(cb, rootTree, rootTree)
            }
            this@RootDelegator.serObject = cb
            return cb
        }

        //////////////////////
        // Delegate methods //
        //////////////////////

        override fun getValue(thisRef: Any?, property: KProperty<*>): ClassBuilder = this

        ///////////////////////////
        // Class Builder methods //
        ///////////////////////////

        override val serObjectObservable = SimpleObjectProperty<ClassBuilder>()

        override var serObject: ClassBuilder = serObjectObservable.value ?: createRealRoot()
            private set(value) {
                serObjectObservable.value = value
                field = value
            }

        /** Note that the item is not pointing at this class builder, but directly at the real root */
        override val item = TreeItem<ClassBuilderNode>().also { item ->
            item.value = FilledClassBuilderNode(fakeRootKey, this, this, item, false)
        }

        override val type = Any::class.type()
        override val key = fakeRootKey
        override val property: PropertyMetadata? = null


        override fun resetChild(
            key: ClassBuilder,
            element: ClassBuilder?,
            restoreDefault: Boolean
        ) {
        }

        override fun createChild(
            key: ClassBuilder,
            init: ClassBuilder?,
            item: TreeItem<ClassBuilderNode>
        ): ClassBuilder {
            return when (key) {
                realRootKey -> serObject
                fakeRootKey -> this
                else -> error("Key supplied ($key) not real root key ($realRootKey) or fake root key $fakeRootKey")
            }
        }

        override fun isImmutable() = true
        override fun getPreviewValue() = "fake root"
        override fun getChildType(key: ClassBuilder): JavaType? {
            return when (key) {
                realRootKey -> serObject.type
                fakeRootKey -> RootDelegator::class.type()
                else -> error("Key supplied ($key) not real root key ($realRootKey) or fake root key $fakeRootKey")
            }
        }

        override fun getChildPropertyMetadata(key: ClassBuilder): PropertyMetadata {
            if (key === realRootKey) {
                return serObject.property ?: error("Failed to find root property")
            }
            error("Key supplied '${key.getPreviewValue()}' not real root key '${realRootKey.getPreviewValue()}' or fake root key '${fakeRootKey.getPreviewValue()}'")

        }

        override fun get(key: ClassBuilder): ClassBuilder? = if (key == realRootKey) serObject else null
        override fun getChildren(): Map<ClassBuilder, ClassBuilder?> = mapOf(realRootKey to serObject)

        override fun createEditView(parent: EventTarget, controller: ObjectEditorController) =
            parent.text("Fake root should be displayed :o")

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as RootDelegator

            if (realRootType != other.realRootType) return false
            if (rootObj != other.rootObj) return false
            if (realRootKey != other.realRootKey) return false

            return true
        }

        override fun hashCode(): Int {
            var result = realRootType.hashCode()
            result = 31 * result + (rootObj?.hashCode() ?: 0)
            return result
        }
    }
}
