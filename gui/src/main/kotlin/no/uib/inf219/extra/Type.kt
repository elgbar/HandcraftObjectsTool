package no.uib.inf219.extra

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.gui.loader.ClassInformation
import kotlin.reflect.KClass

/**
 * @author Elg
 */

/**
 * Convert a java class into a [JavaType] with [ClassInformation]
 */
fun Class<*>.type(): JavaType {
    return ClassInformation.toJavaType(this)
}

/**
 * Convert a kotlin class into a [JavaType] with [ClassInformation]
 */
fun KClass<*>.type(): JavaType {
    return this.java.type()
}
