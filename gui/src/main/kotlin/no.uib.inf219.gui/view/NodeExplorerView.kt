package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.DataManager
import no.uib.inf219.gui.GuiMain
import no.uib.inf219.gui.components.AttributeComp
import no.uib.inf219.gui.components.PartComp
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(val controller: ObjectEditorController) : View("Tree Explorer") {
    override val root =
        treeview<Pair<PartComp?, AttributeComp?>> {
            root = TreeItem(Pair(controller.part, controller.attr))
            root.isExpanded = true
            cellFormat {
                val (part, attr) = it
                val fullPath = attr?.path ?: part?.className ?: "Failed to find any path or classname"
                text = fullPath.substring(fullPath.lastIndexOf('.') + 1)
                tooltip(
                    attr?.className ?: part?.className
                    ?: "Does your working directory contain the metadata? (currently the program is hard coded to only look at '${GuiMain.MAIN_CLASS}')"
                )


                onDoubleClick {
                    controller.attr = it.second
                    controller.part = it.first
//                        throw IllegalStateException(it.second.toString() + "" + controller.attr)
//                        controller.attr = it.second

                }

//                    action {
//                        replaceWith<PageTwo>(ViewTransition.Slide(0.3.seconds, Direction.LEFT)
//                    }
            }
            populate { parent ->
                val (part, attr) = parent.value;

                if (attr != null && attr.isList && DataManager.PARTS.containsKey(attr.className)) {
                    val pp = DataManager.PARTS[attr.className]!!
                    DataManager.findChildren(pp)
                } else if (part != null) {
                    DataManager.findChildren(part)
                } else {
                    //normal attribute
                    null
                }
            }
        }


}
