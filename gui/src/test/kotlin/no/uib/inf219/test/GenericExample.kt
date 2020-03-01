package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Elg
 */
data class GenericExample(@JsonProperty("prerequisite", required = true) val pre: Prerequisite) {}
