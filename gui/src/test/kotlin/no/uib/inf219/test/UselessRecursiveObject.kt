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
}
