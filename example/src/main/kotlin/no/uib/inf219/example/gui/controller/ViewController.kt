package no.uib.inf219.example.gui.controller

import javafx.beans.property.SimpleObjectProperty
import no.uib.inf219.example.data.Conversation
import no.uib.inf219.example.gui.Main
import tornadofx.getValue
import tornadofx.setValue

/**
 * @author Elg
 */
class ViewController {

    val convProperty = SimpleObjectProperty<Conversation>(Main.TEST_CONV)
    var conv: Conversation by convProperty


}
