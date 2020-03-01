package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonIgnore
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.OutputArea

/**
 * A class builder that is able to be referenced by [ReferenceClassBuilder]. This enables [toObject] to return the same object multiple times
 *
 * @author Elg
 */
abstract class ReferencableClassBuilder<out T> : ClassBuilder<T> {

    @JsonIgnore
    private var dirty: Boolean = true
    @JsonIgnore
    private var objCache: T? = null

    override fun isDirty() = dirty

    override fun toObject(): T? {
        if (dirty || objCache == null) {
            objCache = ControlPanelView.mapper.convertValue(serializationObject, type)
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
