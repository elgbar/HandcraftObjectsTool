package no.uib.inf219.gui.view

import javafx.collections.ObservableList
import no.uib.inf219.gui.backend.ClassBuilder
import tornadofx.Fragment
import tornadofx.borderpane
import tornadofx.tableview

/**
 * @author Elg
 */
class IndexableFragment(list: ObservableList<ClassBuilder<Any>>) : Fragment() {

    override val root = borderpane {
        left = tableview<ClassBuilder<Any>> {
            //            column("Name", ClassBuilder<*>::name)
        }
    }
}
