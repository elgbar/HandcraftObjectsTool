package no.uib.inf219.gui.view

import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.View
import tornadofx.hbox

/**
 * @author Elg
 */
class NodeExplorerView(val controller: ObjectEditorController) : View("Tree Explorer") {
    override val root = hbox()
//        treeview<Pair<PartComp?, AttributeComp?>> {
//            root = TreeItem(Pair(controller.part, controller.attr))
//            root.isExpanded = true
//            cellFormat {
//                val (part, attr) = it
//                val fullPath = attr?.path ?: part?.className ?: "Failed to find any path or classname"
//                text = fullPath.substring(fullPath.lastIndexOf('.') + 1)
//                tooltip(
//                    attr?.className ?: part?.className
//                    ?: "Does your working directory contain the metadata?)"
//                )
//
//                //select the clicked node as currently editing
//                onDoubleClick {
//                    controller.attr = it.second
//                    controller.part = it.first
////                        throw IllegalStateException(it.second.toString() + "" + controller.attr)
////                        controller.attr = it.second
//
//                }
//
////                    action {
////                        replaceWith<PageTwo>(ViewTransition.Slide(0.3.seconds, Direction.LEFT)
////                    }
//            }
//            populate { parent ->
//                val (part, attr) = parent.value;
//
//                if (attr != null && attr.isList && DataManager.PARTS.containsKey(attr.className)) {
//                    val pp = DataManager.PARTS[attr.className]!!
//                    DataManager.findChildren(pp)
//                } else if (part != null) {
//                    DataManager.findChildren(part)
//                } else {
//                    //normal attribute
//                    null
//                }
//            }
//        }


}
