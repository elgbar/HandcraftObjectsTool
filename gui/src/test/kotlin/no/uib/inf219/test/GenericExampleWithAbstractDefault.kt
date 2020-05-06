package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.test.precondition.Precondition

/**
 * @author Elg
 */
class GenericExampleWithAbstractDefault(
    @JsonProperty(
        "prerequisite",
        required = true,
        defaultValue = "{\"class\":\"no.uib.inf219.test.precondition.AlwaysFalsePrecondition\"}"
    ) val pre: Precondition?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenericExampleWithAbstractDefault

        if (pre != other.pre) return false

        return true
    }

    override fun hashCode(): Int {
        return pre?.hashCode() ?: 0
    }
}
