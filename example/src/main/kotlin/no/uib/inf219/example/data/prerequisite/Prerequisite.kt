package no.uib.inf219.example.data.prerequisite

import no.uib.inf219.api.serialization.Serializable

/**
 * @author Elg
 */
interface Prerequisite : Serializable {

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
