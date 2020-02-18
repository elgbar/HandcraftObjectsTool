package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.ObjectProperty
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ComplexClassBuilder
import no.uib.inf219.gui.view.OutputArea
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.getProperty
import tornadofx.property


/**
 * @author Elg
 */
class ObjectEditorController(
    root: JavaType,
    val rootBuilder: ClassBuilder<Any> = ComplexClassBuilder(root),
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

        OutputArea.logln("reloading for $this (parent $parent)")

        //To visually display the newly created element we need to rebuild the TreeView in NodeExplorerView
        // It is rebuilt when controller.currSel, so we change the currently viewed to the root then back to this view
        // In other words we turn it off then on again
        val curr = currSel
        currSel = rootSel
        currSel = curr
    }

    fun select(pair: Pair<String, ClassBuilder<*>?>) {
        select(pair.first, pair.second)
    }

    fun select(name: String, classBuilder: ClassBuilder<*>?) {
        currSel = MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>(name, classBuilder, classBuilder?.parent)
    }
}
