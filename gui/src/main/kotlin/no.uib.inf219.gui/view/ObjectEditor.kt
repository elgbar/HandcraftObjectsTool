package no.uib.inf219.gui.view

import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.View
import tornadofx.borderpane

/**
 * The view of the main editor
 *
 *
 *
 * @param clazz The class we are editing
 * @author Elg
 */
class ObjectEditor(val clazz: Class<Any>) : View() {

    val controller = ObjectEditorController(clazz)

    override val root = borderpane {
        left = NodeExplorerView(controller).root
        center = AttributeEditor(controller).root
    }

}
