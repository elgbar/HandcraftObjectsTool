package no.uib.inf219.gui.controllers

import javafx.beans.property.ObjectProperty
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.ComplexClassBuilder
import no.uib.inf219.gui.loader.ClassInformation
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property


/**
 * @author Elg
 */
class ObjectEditorController(root: Class<*>) : Controller() {

    val rootBuilder: ClassBuilder<Any> = ComplexClassBuilder(ClassInformation.toJavaType(root))

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
        currSel = MutableTriple(root.simpleName, rootBuilder, rootBuilder)
    }


}
