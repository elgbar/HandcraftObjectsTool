package no.uib.inf219.example.data.prerequisite

import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * @author Elg
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
interface Prerequisite {

    /**
     * Check if this prerequisite is fulfilled
     *
     * @return `true` if this prerequisite is fulfilled
     */
    fun check(): Boolean

    /**
     * @return the reason for why this prerequisite is/is not fulfilled.
     */
    fun reason(): String
}
