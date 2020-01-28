package no.uib.inf219.gui.controllers

import javafx.beans.property.ObjectProperty
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.MapClassBuilder
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property


/**
 * @author Elg
 */
class ObjectEditorController(val root: Class<*>) : Controller() {

    val rootBuilder: ClassBuilder<Any> = MapClassBuilder(ClassInformation.toJavaType(root))

    /**
     * Left type is name of selected
     *
     * Right is type
     */
    var currSel: Pair<String, ClassBuilder<*>> by property<Pair<String, ClassBuilder<*>>>()
    var currProp: ObjectProperty<Pair<String, ClassBuilder<*>>> = getProperty(ObjectEditorController::currSel)

    init {
        currSel = Pair(root.simpleName, rootBuilder)
    }


}
