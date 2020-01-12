package no.uib.inf219.example.data.prerequisite

/**
 * @author Elg
 */
interface Prerequisite {

    fun check(): Boolean

    fun reason(): String
}
