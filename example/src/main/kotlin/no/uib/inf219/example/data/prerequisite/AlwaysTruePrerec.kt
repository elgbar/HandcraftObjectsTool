package no.uib.inf219.example.data.prerequisite

/**
 * @author Elg
 */
class AlwaysTruePrerec : Prerequisite {
    override fun check(): Boolean {
        return true
    }

    override fun reason(): String {
        return "This prerequisite is always true"
    }
}
