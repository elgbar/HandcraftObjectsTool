package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.selectedItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.controllers.classBuilderNode.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation.PropertyMetadata
import tornadofx.text
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * @author Elg
 */
class ObjectEditorController(
    rootType: JavaType
) {

    lateinit var tree: TreeView<ClassBuilderNode>

    /**
     * The fake root
     *
     * @see RootDelegator
     */
    private val fakeRoot by RootDelegator(rootType)
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
            val newCbn = item.value.ensurePresentClassBuilder(this) ?: return null
            item.value = newCbn
            return newCbn
        }
    }

    companion object {
        val fakeRootKey = "root".toCb()
    }

    /**
     * Delegator that handles resetting of our real root. It acts as any other class builder is expected to behave: It is a parent class builder with one child with key [fakeRootKey] and the value [serObject]. The real root can never be `null` so when asked to reset it ignores `restoreDefault` and behaves as if it is always `true`. The default value of the root is what is returned when calling [ClassBuilder.createClassBuilder] with type [realRootType].
     */
    private class RootDelegator(private val realRootType: JavaType) : ParentClassBuilder(),
        ReadOnlyProperty<Any?, ClassBuilder> {

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
            val cb = ClassBuilder.createClassBuilder(realRootType, realRootKey, this, rootPropMeta, TreeItem())
                ?: error("failed to create a root class builder")
            item.children.setAll(cb.item)

            this@RootDelegator.serObject = cb
            return cb
        }

        //////////////////////
        // Delegate methods //
        //////////////////////

        override fun getValue(thisRef: Any?, property: KProperty<*>): ClassBuilder {
            return this
        }

        ///////////////////////////
        // Class Builder methods //
        ///////////////////////////

        override val serObjectObservable = SimpleObjectProperty<ClassBuilder>()

        override var serObject: ClassBuilder
            get() = serObjectObservable.value ?: createRealRoot()
            private set(value) {
                serObjectObservable.value = value
            }

        /** Key to the real root */
        val realRootKey = (realRootType.rawClass?.simpleName ?: realRootType.typeName).toCb()

        /** Note that the item is not pointing at this class builder, but directly at the real root */
        override val item: TreeItem<ClassBuilderNode> by lazy {
            val item = TreeItem<ClassBuilderNode>()
            val cbn = FilledClassBuilderNode(fakeRootKey, this, this, item)
            item.value = cbn
            return@lazy item
        }

        override val type = Any::class.type()
        override val parent = this
        override val key = fakeRootKey
        override val property: PropertyMetadata? = null


        override fun resetChild(
            key: ClassBuilder,
            element: ClassBuilder?,
            restoreDefault: Boolean
        ) {
//            require(key == realRootKey) { "Key does not match the real root key '${realRootKey.serObject}'" }
//            require(element == serObject) { "Element does not match the current root '${serObject}'" }
//            return createRealRoot().item.value
        }

        override fun createChildClassBuilder(
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

        override fun getChildPropertyMetadata(key: ClassBuilder): PropertyMetadata? {
            return when (key) {
                realRootKey -> serObject.property
                fakeRootKey -> null
                else -> error("Key supplied ($key) not real root key ($realRootKey) or fake root key $fakeRootKey")
            }
        }

        override fun getChild(key: ClassBuilder): ClassBuilder? = if (key == realRootKey) serObject else null
        override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder?> = mapOf(realRootKey to serObject)

        override fun createEditView(parent: EventTarget, controller: ObjectEditorController) =
            parent.text("Fake root should be displayed :o")
    }
}
