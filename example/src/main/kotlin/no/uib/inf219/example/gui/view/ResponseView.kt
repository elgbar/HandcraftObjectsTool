package no.uib.inf219.example.gui.view

import no.uib.inf219.example.data.Response
import tornadofx.View
import tornadofx.borderpane
import tornadofx.label

/**
 * @author Elg
 */
class ResponseView(response: Response) : View("Response ${response.name}") {
    override val root = borderpane {
        label {
            text = response.text
        }
    }
}
