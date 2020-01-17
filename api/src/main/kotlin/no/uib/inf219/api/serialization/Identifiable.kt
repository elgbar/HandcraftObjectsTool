package no.uib.inf219.api.serialization

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.ObjectIdGenerators

/**
 * @author Elg
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
interface Identifiable<T> {

    /**
     * Unique id of this object
     */
    @JsonIgnore
    fun getId(): T
}
