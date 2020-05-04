package no.uib.inf219.extra

import com.fasterxml.jackson.databind.JavaType
import no.uib.inf219.gui.loader.ClassInformation
import kotlin.reflect.KClass

/**
 * @author Elg
 */

/**
 * Convert a java class into a [JavaType] with [ClassInformation]
 *
 * The given type is always treated as a java object type to prevent confusion with primitives, as they do not really what type they are when serializing.
 */
fun Class<*>.type(): JavaType {
    val objType = if (this.isPrimitive) this.kotlin.javaObjectType else this

    val type = ClassInformation.toJavaType(objType)
    return if (type.isContainerType) {
        type.withContentType(type.contentType.toObjType())
    } else type
}

/**
 * Convert a kotlin class into a [JavaType] with [ClassInformation].
 *
 * The given type is always treated as a java object type to prevent confusion with primitives, as they do not really what type they are when serializing.
 */
fun KClass<*>.type(): JavaType {
    return this.javaObjectType.type()
}

fun JavaType.toObjType(): JavaType {
    return if (this.rawClass.isPrimitive) rawClass.type() else this
}

fun JavaType.isTypeOrSuperTypeOfPrimAsObj(type: JavaType): Boolean {
    return this.isTypeOrSuperTypeOfPrimAsObj(type.rawClass)
}

fun JavaType.isTypeOrSuperTypeOfPrimAsObj(clazz: Class<*>): Boolean {
    return this.isTypeOrSuperTypeOfPrimAsObj(clazz.kotlin)
}

fun JavaType.isTypeOrSuperTypeOfPrimAsObj(clazz: KClass<*>): Boolean {
    val thisObjType = this.rawClass.kotlin.javaObjectType
    val thatObjType = clazz.javaObjectType
    return thisObjType == thatObjType || thisObjType.isAssignableFrom(thatObjType)
}
