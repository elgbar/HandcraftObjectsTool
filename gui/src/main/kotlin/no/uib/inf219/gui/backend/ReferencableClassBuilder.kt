package no.uib.inf219.gui.backend

import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.OutputArea

/**
 * A classbuilder that is able to be referenced by [ReferenceClassBuilder]. This enables [toObject] to return the same object multiple times
 *
 * @author Elg
 */
abstract class ReferencableClassBuilder<out T> : ClassBuilder<T> {

    private var dirty: Boolean = true
    private var objCache: T? = null

    override fun toObject(): T? {
        if (dirty || objCache == null) {
            objCache = ControlPanelView.mapper.convertValue(toTree(), type)
            dirty = false
        }
        return objCache
    }

    override fun recompile() {
        parent?.recompile()
        dirty = true
        OutputArea.logln("${this::class.java.simpleName} is dirty!")
    }
}
