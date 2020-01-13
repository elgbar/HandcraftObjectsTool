package no.uib.inf219.example.data.prerequisite

import no.uib.inf219.api.serialization.Serializable

/**
 * @author Elg
 */
interface Prerequisite : Serializable {

    fun check(): Boolean

    fun reason(): String
}
