package no.uib.inf219.gui.view

import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.Fragment
import tornadofx.borderpane
import tornadofx.onUserSelect

/**
 * @author Elg
 */
class PropertyEditor : Fragment("Attribute Editor") {

    internal val controller: ObjectEditorController by param()

    override val root = borderpane {
        controller.tree.onUserSelect {
            center = it.cb?.toView(this, controller)
        }
    }
}
