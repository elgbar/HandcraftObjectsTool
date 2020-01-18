package no.uib.inf219.gui.controllers

import javafx.beans.property.SimpleObjectProperty
import no.uib.inf219.gui.DataManager
import no.uib.inf219.gui.GuiMain
import no.uib.inf219.gui.components.AttributeComp
import no.uib.inf219.gui.components.PartComp
import tornadofx.getValue
import tornadofx.setValue

/**
 * @author Elg
 */
class ObjectEditorController(val clazz: Class<Any>) {

    val partProperty = SimpleObjectProperty<PartComp>(
        DataManager.PARTS[GuiMain.MAIN_CLASS]
    )
    var part by partProperty

    val attrProperty = SimpleObjectProperty<AttributeComp>()
    var attr by attrProperty


}
