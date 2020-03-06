package no.uib.inf219.gui.backend

import no.uib.inf219.gui.view.ControlPanelView.mapper


/**
 * A class builder that is able to be referenced by [ReferenceClassBuilder]. This enables [toObject] to return the same object multiple times
 *
 * @author Elg
 */
abstract class ReferencableClassBuilder<out T> : ClassBuilder<T> {

    private var dirty: Boolean = true

    private var objCache: T? = null

    override fun isDirty() = dirty

    override fun toObject(): T? {
        if (dirty || objCache == null) {

            println("built = ${mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)}")

            objCache = mapper.convertValue(this, type)

            dirty = false
        }
        return objCache
    }

    override fun recompile() {
        parent?.recompile()
        dirty = true
    }
}
