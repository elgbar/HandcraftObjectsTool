package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ParentClassBuilder
import no.uib.inf219.gui.view.NodeExplorerView.Companion.repopulate
import tornadofx.getProperty
import tornadofx.property
import tornadofx.text
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * @author Elg
 */
class ObjectEditorController(
    rootType: JavaType,

    /**
     * Parent controller, if any
     */
    val parentController: ObjectEditorController? = null
) {

    lateinit var tree: TreeView<ClassBuilderNode>

    /**
     * The fake root.
     *
     * @see RootDelegator
     */
    val rootCb by RootDelegator(rootType)

    /**
     * The currently selected node. Should only be set node explorer
     */
    var currSel: ClassBuilderNode? by property<ClassBuilderNode>()

    val currProp: ObjectProperty<ClassBuilderNode?> = getProperty(ObjectEditorController::currSel)

    val rootSel: ClassBuilderNode = rootCb.toCBN()

    fun reloadView() {

        //To visually display the newly created element we need to rebuild the TreeView in NodeExplorerView
        // It is rebuilt when controller.currSel, so we change the currently viewed to the root then back to this view
        // In other words we turn it off then on again
        currSel = null
        currSel = rootSel
        tree.repopulate()

    }

    fun select(cb: ClassBuilder) {
        currSel = cb.toCBN()
    }

    /**
     * Find the top level root OE controller
     */
    fun findRootController(): ObjectEditorController = parentController?.findRootController() ?: this

    companion object {
        val fakeRootKey = "root".toCb()

        fun ClassBuilder.toCBN(): ClassBuilderNode {
            val key = this.key
            val parent = this.parent

            return FilledClassBuilderNode(key, this, parent)
        }
    }

    /**
     * Delegator that handles resetting of our real root. It acts as any other class builder is expected to behave: It is a parent class builder with one child with key [fakeRootKey] and the value [serObject]. The real root can never be `null` so when asked to reset it ignores `restoreDefault` and behaves as if it is always `true`. The default value of the root is what is returned when calling [ClassBuilder.createClassBuilder] with type [realRootType].
     */
    private class RootDelegator(private val realRootType: JavaType) : ParentClassBuilder(),
        ReadWriteProperty<Any?, ClassBuilder> {

        /**
         * (re)create the root class builder
         */
        private fun createRealRoot(): ClassBuilder {
            val cb = ClassBuilder.createClassBuilder(realRootType, fakeRootKey, this, null)
                ?: error("failed to create a root class builder")
            this@RootDelegator.serObject = cb
            return cb
        }

        //////////////////////
        // Delegate methods //
        //////////////////////

        override fun getValue(thisRef: Any?, property: KProperty<*>): ClassBuilder {
            return serObject
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: ClassBuilder) {
            throw IllegalArgumentException("Cannot set class builder root directly")
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

        override val item: TreeItem<ClassBuilderNode> = TreeItem(FilledClassBuilderNode(fakeRootKey, this, this))
        override val type = Any::class.type()
        override val parent = this
        override val key = fakeRootKey
        override val property = null

        /**
         * Key to the real root
         */
        val realRootKey = (realRootType.rawClass?.simpleName ?: realRootType.typeName).toCb()
        private var realRootCBN = FilledClassBuilderNode(realRootKey, serObject, this)

        override fun resetChild(
            key: ClassBuilder,
            element: ClassBuilder?,
            restoreDefault: Boolean
        ): ClassBuilderNode {
            realRootCBN = FilledClassBuilderNode(realRootKey, createRealRoot(), this)
            return realRootCBN
        }

        override fun createClassBuilderFor(key: ClassBuilder, init: ClassBuilder?) = serObject
        override fun isRequired() = true // it's kinda hard to create something without this
        override fun isImmutable() = true
        override fun getPreviewValue() = "null"
        override fun getChildType(cb: ClassBuilder) = serObject.type
        override fun getChild(key: ClassBuilder): ClassBuilder? = if (key == realRootKey) serObject else null
        override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder?> = mapOf(realRootKey to serObject)

        override fun toView(parent: EventTarget, controller: ObjectEditorController) =
            parent.text("Fake root should be displayed")

    }
}
