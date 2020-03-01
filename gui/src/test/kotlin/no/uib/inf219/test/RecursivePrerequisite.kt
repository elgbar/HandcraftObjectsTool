package no.uib.inf219.test

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * @author Elg
 */
data class RecursivePrerequisite @JsonCreator constructor(@JsonValue var with: Prerequisite) : Prerequisite {

    override fun check(): Boolean {
        return true
    }

    override fun reason(): String {
        return "This prerequisite is always true"
    }
}
