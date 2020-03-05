package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.ObjectProperty
import no.uib.inf219.gui.backend.ClassBuilder
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.getProperty
import tornadofx.property


/**
 * @author Elg
 */
class ObjectEditorController(
    root: JavaType,
    val rootBuilder: ClassBuilder<*> = ClassBuilder.getClassBuilder(root, "root", null)!!,
    /**
     * Parent controller, if any
     */
    val parent: ObjectEditorController? = null
) {


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
        MutableTriple(root.rawClass?.simpleName ?: root.typeName, rootBuilder, rootBuilder)

    init {
        currSel = rootSel
    }

    fun reloadView() {

        //To visually display the newly created element we need to rebuild the TreeView in NodeExplorerView
        // It is rebuilt when controller.currSel, so we change the currently viewed to the root then back to this view
        // In other words we turn it off then on again
        val curr = currSel
        currSel = rootSel
        currSel = curr
    }

    fun select(classBuilder: ClassBuilder<*>) {
        currSel = MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>(
            classBuilder.name,
            classBuilder,
            classBuilder.parent!!
        )
    }

    fun select(name: String, classBuilder: ClassBuilder<*>?) {
        currSel = MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>(name, classBuilder, classBuilder?.parent!!)
    }

    /**
     * Find the top level root OE controller
     */
    fun findRootController(): ObjectEditorController = parent?.findRootController() ?: this
}
