package no.uib.inf219.gui.backend.serializers

import no.uib.inf219.gui.backend.ClassBuilder
import no.uib.inf219.gui.backend.serializers.ClassBuilderCompiler.Companion.build
import java.util.*
import kotlin.collections.HashMap


/**
 * Allow for recursive mapping from class builder the object it is representing.
 * This is done by first calling [ClassBuilder.compile] to create all objects, filling any references with the dummy [UUID] given in [getValue]
 *
 * Once the object creation is done [ClassBuilder.link] will make sure all the references are filled in.
 *
 * The end user only need to call [build] with the desired class builder. It will compile and link the class builder to a format that can be
 * converted to
 *
 * @author Elg
 */
class ClassBuilderCompiler private constructor() {

    private val ref2cb = HashMap<ClassBuilder<*>, UUID>()
    private val cache = HashMap<UUID, Any?>()

    companion object {

        /**
         * Format the given class builder in a way that is easier to convert to the original class.
         */
        fun build(initCB: ClassBuilder<*>): Any {
            val cbc = ClassBuilderCompiler()
            val compiled = cbc.compile(initCB)
            initCB.link(cbc, compiled)
            return compiled
        }
    }

    internal fun setValue(cb: ClassBuilder<*>, value: Any?) {
        val ref = ref2cb.computeIfAbsent(cb) { UUID.randomUUID() }
        cache[ref] = value
    }

    internal fun getValue(cb: ClassBuilder<*>): Pair<UUID, Any?>? {
        val ref = ref2cb[cb] ?: return null
        return ref to cache[ref]
    }

    internal fun resolveReference(ref: Any): Any {
        return if (ref is UUID) cache[ref] ?: ref else ref
    }

    internal fun compile(cb: ClassBuilder<*>): Any {
        val pair = getValue(cb)
        return if (pair == null) {
            setValue(cb, null)
            val compiled = cb.compile(this)
            //after compiling the object update the cache
            setValue(cb, compiled)

            compiled
        } else {
            //this has already been seen so we return the uuid to be resolved with linker
            pair.first
        }
    }
}
