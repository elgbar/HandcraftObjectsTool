package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.test.precondition.Precondition

/**
 * @author Elg
 */
data class GenericExample(@JsonProperty("prerequisite", required = true) val pre: Precondition) {}
