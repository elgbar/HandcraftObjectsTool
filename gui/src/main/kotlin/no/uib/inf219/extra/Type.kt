/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.extra

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
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

operator fun JsonNode.get(key: ClassBuilder): JsonNode? = when (key.serObject) {
    is String -> this[key.serObject as String]
    is Int -> this[key.serObject as Int]
    else -> error("Key not string or int: $key")
}
