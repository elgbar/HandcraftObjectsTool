package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.beans.property.ObjectProperty
import javafx.event.EventTarget
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ClassBuilder
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.getProperty
import tornadofx.property
import tornadofx.text
import tornadofx.toProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * @author Elg
 */
class ObjectEditorController(
    rootType: JavaType,
    initRoot: ClassBuilder<*>?,

    /**
     * Parent controller, if any
     */
    val parentController: ObjectEditorController? = null
) {

    var rootCb: ClassBuilder<*> by RootDelegator(rootType, initRoot, this)
        private set

    /**
     * Left type is name of selected
     *
     * middle is current type
     *
     * Right is parent type
     */
    var currSel: MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>? by property<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>>()

    var currProp: ObjectProperty<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>?> =
        getProperty(ObjectEditorController::currSel)

    val rootSel: MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>> =
        MutableTriple(rootType.rawClass?.simpleName ?: rootType.typeName, rootCb, rootCb.parent!!)

    init {
        currSel = rootSel
    }

    fun reloadView() {

        //To visually display the newly created element we need to rebuild the TreeView in NodeExplorerView
        // It is rebuilt when controller.currSel, so we change the currently viewed to the root then back to this view
        // In other words we turn it off then on again
        val curr = currSel
        currSel = null
        currSel = curr
    }

    fun select(classBuilder: ClassBuilder<*>) {
        currSel = MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>(
            classBuilder.key!!.getPreviewValue(), classBuilder, classBuilder.parent!!
        )
    }

    /**
     * Find the top level root OE controller
     */
    fun findRootController(): ObjectEditorController = parentController?.findRootController() ?: this

    fun updateRoot() {
        @Suppress("UNNECESSARY_SAFE_CALL") //not unnecessary as this will be called by the root cb delegator before being initialized
        rootSel?.middle = rootCb
    }

    companion object {
        val fakeRootKey = "root".toCb()
    }

    private class RootDelegator(
        private val realRootType: JavaType,
        private var realRoot: ClassBuilder<*>? = null,
        private val controller: ObjectEditorController
    ) :
        ClassBuilder<Any>,
        ReadWriteProperty<Any?, ClassBuilder<*>> {

        val rootProp = realRoot.toProperty()

        private val rootKey: ClassBuilder<*>
        private val rootParent: ClassBuilder<*>

        init {
            val initRoot = realRoot
            if (initRoot != null) {
                rootKey = initRoot.key ?: error("Object editor controller was given an initial root, but it had no key")
                rootParent =
                    initRoot.parent ?: error("Object editor controller was given an initial root, but it had no parent")
                require(rootParent.getChild(rootKey) === realRoot) {
                    "Object editor controller was given an initial root with non-null parent and key, " +
                            "but the given parent does not return the given root when using it's key!. " +
                            "Expected $initRoot, but got ${rootParent.getChild(rootKey)} from parent"
                }
            } else {
                rootKey = fakeRootKey
                rootParent = this
            }
        }

        private fun getOrCreateRoot(): ClassBuilder<*> {
            return realRoot ?: createClassBuilderFor(fakeRootKey)
        }

        //////////////////////
        // Delegate methods //
        //////////////////////


        override fun getValue(thisRef: Any?, property: KProperty<*>): ClassBuilder<*> {
            return getOrCreateRoot()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: ClassBuilder<*>) {
            throw IllegalArgumentException("Cannot set class builder root directly")
        }

        /////////////////////
        //   CB  methods   //
        /////////////////////

        override val serObject = getOrCreateRoot()
        override val serObjectObservable = getOrCreateRoot().toProperty()
        override val type = Any::class.type()
        override val parent: ClassBuilder<*>? = null
        override val key: ClassBuilder<*>? = null
        override val property: PropertyWriter? = null

        override fun isLeaf(): Boolean = false
        override fun getPreviewValue() = "Fake root"
        override fun getChildType(cb: ClassBuilder<*>) = getOrCreateRoot().type
        override fun getChild(key: ClassBuilder<*>) = getOrCreateRoot()
        override fun isImmutable() = true
        override fun toView(parent: EventTarget, controller: ObjectEditorController) =
            parent.text("Fake root cannot be displayed")

        override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*> {
            if (init == null && realRoot == null) {
                realRoot = ClassBuilder.getClassBuilder(realRootType, fakeRootKey, this, null)
                    ?: error("failed to create a root class builder")
                controller.updateRoot()
            }
            return realRoot!!
        }

        override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?, restoreDefault: Boolean) {
            if (restoreDefault) realRoot = null
            else {
                val root = getOrCreateRoot()
                for ((childKey, value) in root.getSubClassBuilders()) {
                    root.resetChild(childKey, value, false)
                }
            }
            controller.updateRoot()
        }

        override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> {
            return mapOf(fakeRootKey to getOrCreateRoot())
        }
    }
}
