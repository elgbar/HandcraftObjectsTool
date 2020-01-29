package no.uib.inf219.gui.view

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.controllers.ObjectEditorController
import org.apache.commons.lang3.tuple.MutableTriple
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(val controller: ObjectEditorController) : View("Tree Explorer") {


    override val root = treeview<MutableTriple<String, ClassBuilder<*>?, ClassBuilder<*>>> {
        root = TreeItem(controller.currSel)
        root.isExpanded = true
        cellFormat {
            text = it.left
            onDoubleClick {
                //first time we click it we want to create it
                if (it.middle == null) {
                    it.middle = it.right.createClassBuilderFor(it.left)
                }
                controller.currSel = it

//                edit(it, this)
//                println("it = ${it}")
            }
        }

        @Suppress("UNCHECKED_CAST")
        populate {
            val cb = it.value.middle
            when {
                cb == null -> null
                cb.isLeaf() -> null
                else -> cb.getSubClassBuilders()?.map { elem ->
                    MutableTriple(elem.key, elem.value, cb)
                }
            }
        }
    }

//    fun edit(parent: Pair<String, JavaType>, parentCell: TreeCell<Pair<String, JavaType>>) {
//
//    }
}
