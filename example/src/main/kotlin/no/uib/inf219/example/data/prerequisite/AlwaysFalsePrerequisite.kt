package no.uib.inf219.example.data.prerequisite

/**
 * @author Elg
 */
class AlwaysFalsePrerequisite : Prerequisite {


    override fun check(): Boolean {
        return false
    }

    override fun reason(): String {
        return "This prerequisite is always false"
    }
}
