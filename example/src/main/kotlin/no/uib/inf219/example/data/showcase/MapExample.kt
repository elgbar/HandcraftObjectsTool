package no.uib.inf219.example.data.showcase

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author Elg
 */
data class MapExample(@JsonProperty("map", required = true) val map: MutableMap<String, String>) {
}
