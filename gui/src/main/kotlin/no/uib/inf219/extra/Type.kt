package no.uib.inf219.extra

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.gui.loader.ClassInformation
import kotlin.reflect.KClass

/**
 * @author Elg
 */
fun Class<*>.type(): JavaType {
    return ClassInformation.toJavaType(this)
}

fun KClass<*>.type(): JavaType {
    return ClassInformation.toJavaType(this.java)
}
