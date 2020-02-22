package no.uib.inf219.extra

import io.github.classgraph.ClassGraph

/**
 * @author Elg
 */

/**
 * Add all given class loaders. This is just a convenient method it internally calls [ClassGraph.addClassLoader] on each of them in the iteration order of [cls]
 *
 * @param cls The class loaders to add
 *
 * @return this for chaining
 */
fun ClassGraph.addClassLoaders(cls: Collection<ClassLoader>): ClassGraph {
    for (it in cls) {
        addClassLoader(it)
    }
    return this
}
