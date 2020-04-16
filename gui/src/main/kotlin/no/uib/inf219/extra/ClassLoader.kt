package no.uib.inf219.extra

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.loader.DynamicClassLoader

/**
 * @author Elg
 */

/**
 * Load a jackson java type from [DynamicClassLoader] with a nullable name, if [name] is `null` the returned value will be `null`
 *
 * @see ClassLoader.loadClass
 * @see ClassInformation.toJavaType
 */
fun DynamicClassLoader.loadType(name: String?): JavaType? {
    return loadType(name ?: return null)
}

/**
 * Load class from [DynamicClassLoader] with a nullable name, if [name] is `null` the returned value will be `null`
 *
 * @see ClassLoader.loadClass
 */
fun DynamicClassLoader.loadClass(name: String?): Class<*>? {
    return loadClass(name ?: return null)
}
