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
