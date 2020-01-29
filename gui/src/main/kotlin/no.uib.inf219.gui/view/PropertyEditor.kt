package no.uib.inf219.gui.view

import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.View
import tornadofx.borderpane
import tornadofx.onChange

/**
 * @author Elg
 */
class PropertyEditor(val controller: ObjectEditorController) : View("Attribute Editor") {

    override val root = borderpane {
        controller.currProp.onChange {
            println("change! viewing -> $it")
            center = it?.middle?.toView(this)
        }
    }
}
