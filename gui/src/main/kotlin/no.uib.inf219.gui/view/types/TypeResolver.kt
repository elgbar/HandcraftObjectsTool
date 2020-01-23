package no.uib.inf219.gui.view.types

import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult

/**
 * Utility method to get [TypeEditor] for any given type, if it exists
 *
 * @author Elg
 */
object TypeResolver {

    private val resolvers: MutableMap<String, TypeEditor<*>> = HashMap()

    init {
        ClassGraph().enableClassInfo().scan().use { scan: ScanResult ->
            val info = scan.getClassInfo(TypeEditor::class.java.name)
            val sub = info.subclasses

            for (ci in sub) {
                val type: String = ci.typeSignature.superclassSignature.typeArguments[0].toString()
                val clazz = ci.loadClass(TypeEditor::class.java)
                resolvers[type] = clazz.kotlin.objectInstance ?: clazz.newInstance()
            }
        }
    }

    /**
     * The type editor for this type, might return `null` if it doesn't find any
     */
    fun <T> resolve(clazz: Class<T>): TypeEditor<T>? {
        return resolvers[clazz.name] as TypeEditor<T>
    }
}
