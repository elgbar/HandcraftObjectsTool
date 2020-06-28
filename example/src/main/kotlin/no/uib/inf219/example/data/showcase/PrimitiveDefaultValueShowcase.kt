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

package no.uib.inf219.example.data.showcase

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A simple class that has all java primitives. There is no use for this class except for showcasing
 *
 * @author Elg
 */
class PrimitiveDefaultValueShowcase @JsonCreator constructor(
    @JsonProperty("int", defaultValue = "42") val int: Int,
    @JsonProperty("long", defaultValue = "46") val long: Long,
    @JsonProperty("double", defaultValue = "0.1") val double: Double,
    @JsonProperty("float", defaultValue = "0.1") val float: Float,
    @JsonProperty("boolean", defaultValue = "true") val boolean: Boolean,
    @JsonProperty("short", defaultValue = "6") val short: Short,
    @JsonProperty("byte", defaultValue = "1") val byte: Byte,
    @JsonProperty("char", defaultValue = "\"a\"") val char: Char,
    @JsonProperty("string", defaultValue = "\"abc\"") val string: String
) {}
