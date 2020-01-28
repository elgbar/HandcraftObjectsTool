package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(val controller: ObjectEditorController) : View("Tree Explorer") {


    override val root = treeview<Pair<String, ClassBuilder<*>>> {
        root = TreeItem(controller.currSel)
        root.isExpanded = true
        cellFormat {
            text = it.first
            onDoubleClick {
                controller.currSel = it
//                edit(it, this)
                println("it = ${it}")
            }
        }
        populate {
            if (it.value.second.isLeaf()) null
            else it.value.second.getSubClassBuilders().toList()
        }
    }

//    fun edit(parent: Pair<String, JavaType>, parentCell: TreeCell<Pair<String, JavaType>>) {
//
//    }
}
