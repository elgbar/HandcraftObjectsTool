package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A simple class that has all java primitives. There is no use for this class except for showcasing
 *
 * @author Elg
 */
data class PrimitiveNoDefaultValueShowcase @JsonCreator constructor(
    @JsonProperty("int") val int: Int,
    @JsonProperty("long") val long: Long,
    @JsonProperty("double") val double: Double,
    @JsonProperty("float") val float: Float,
    @JsonProperty("boolean") val boolean: Boolean,
    @JsonProperty("short") val short: Short,
    @JsonProperty("byte") val byte: Byte,
    @JsonProperty("char") val char: Char,
    @JsonProperty("string") val string: String
) {}
