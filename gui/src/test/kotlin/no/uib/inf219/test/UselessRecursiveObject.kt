package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators

/**
 * @author Elg
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class)
class UselessRecursiveObject {

    @JsonProperty("with")
    var with: UselessRecursiveObject? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UselessRecursiveObject

        if (with !== other.with) return false

        return true
    }

    override fun toString(): String {
        return "UselessRecursiveObject(hashCode=${hashCode()}, with=${with.hashCode()})"
    }


}
