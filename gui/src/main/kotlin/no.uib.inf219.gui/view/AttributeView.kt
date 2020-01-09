package no.uib.inf219.gui.view

import javafx.scene.control.TableView
import no.uib.inf219.gui.components.AttributeComp
import no.uib.inf219.gui.components.PartComp
import tornadofx.View
import tornadofx.column
import tornadofx.tableview

/**
 * @author Elg
 */
class AttributeView(part: PartComp) : View("Attribute") {

    override val root: TableView<AttributeComp> = tableview(part.attributes) {
        column("Path", AttributeComp::pathProperty)
        column("Required", AttributeComp::requiredProperty)
        column("Default Value", AttributeComp::defaultValueProperty)
        column("Note", AttributeComp::noteProperty)
        column("Is List", AttributeComp::isListProperty)
        column("Class Name", AttributeComp::classNameProperty)
    }
}

