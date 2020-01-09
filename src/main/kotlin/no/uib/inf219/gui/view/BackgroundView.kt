package no.uib.inf219.gui.view

import no.uib.inf219.gui.controllers.ViewController
import tornadofx.View
import tornadofx.borderpane

/**
 * @author Elg
 */
class BackgroundView : View("top") {

    val controller: ViewController =
        ViewController()

    override val root = borderpane {

        left = NodeExplorerView(controller).root
        center = AttributeEditor(controller).root
    }
}

