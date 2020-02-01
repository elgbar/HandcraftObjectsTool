package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.ObjectProperty
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ComplexClassBuilder
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.getProperty
import tornadofx.property


/**
 * @author Elg
 */
class ObjectEditorController(root: JavaType) {

    val rootBuilder: ClassBuilder<Any> = ComplexClassBuilder(root)

    /**
     * Left type is name of selected
     *
     * middle is current type
     *
     * Right is parent type
     */
    var currSel: MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>> by property<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>>()
    var currProp: ObjectProperty<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>> =
        getProperty(ObjectEditorController::currSel)

    init {
        currSel = MutableTriple(root.rawClass?.simpleName ?: root.typeName, rootBuilder, rootBuilder)
    }


}
