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
            center = it?.middle?.toView(this)
        }

        //        check(controller.attr == null && controller.attr!!.isList) { "Cannot edit a list of attribute with this view" }


//        center = textarea(controller.attrProperty.select(AttributeComp::valueProperty)) {
//
//            //TODO make required when attr is required
//            //@see https://github.com/edvin/tornadofx-guide/blob/c1f164ff2e50d98fbd2167df14d7bb09e00ebeb6/part1/11.%20Editing%20Models%20and%20Validation.md
//
//            isEditable = true
//            wrapTextProperty().value = true
//        }
//
//        bottom = borderpane {
//            padding = insets(5)
//
//            top = vbox {
//                text("Note: ")
//                textarea(controller.attrProperty.select(AttributeComp::noteProperty)) {
//                    isEditable = false
//                    prefRowCount = 3
//                    wrapTextProperty().value = true
//                }
//            }
//
//            bottom = vbox() {
//                text("Class: ")
//                textfield(controller.attrProperty.select(AttributeComp::classNameProperty)) {
//                    isEditable = false
//                }
//                text("Path: ")
//                textfield(controller.attrProperty.select(AttributeComp::pathProperty)) {
//                    isEditable = false
//                }
//            }
//
//        }
    }
}
