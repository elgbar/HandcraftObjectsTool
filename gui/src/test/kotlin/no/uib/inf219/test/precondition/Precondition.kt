package no.uib.inf219.test.precondition

import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * @author Elg
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "class")
interface Precondition {

    /**
     * Check if this prerequisite is fulfilled
     *
     * @return `true` if this prerequisite is fulfilled
     */
    fun check(): Boolean
}
