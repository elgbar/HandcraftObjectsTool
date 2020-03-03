package no.uib.inf219.gui.backend.serializers

import no.uib.inf219.gui.backend.ClassBuilder
import java.util.*
import kotlin.collections.HashMap


/**
 * @author Elg
 */
class ClassBuilderSerializer {

    private val keys = HashMap<ClassBuilder<*>, UUID>()
    private val values = HashMap<UUID, Any>()

    /**
     * Get the ID of the given object
     */
    fun getId(cb: ClassBuilder<*>): UUID {
        return keys.computeIfAbsent(cb) { UUID.randomUUID() }
    }

    fun seen(cb: ClassBuilder<*>): Boolean {
        return keys.containsKey(cb)
    }

    fun setValue(cb: ClassBuilder<*>, value: Any) {
        require(seen(cb))
        values[keys[cb]!!] = value
    }

    fun getValue(cb: ClassBuilder<*>): Any? {
        return values[keys[cb]]
    }
}
