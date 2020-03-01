package no.uib.inf219.example.data.showcase

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.example.data.prerequisite.Prerequisite

/**
 * @author Elg
 */
data class GenericExample(@JsonProperty("prerequisite", required = true) val pre: Prerequisite) {
}
