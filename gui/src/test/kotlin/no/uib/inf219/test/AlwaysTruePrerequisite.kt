package no.uib.inf219.test

/**
 * @author Elg
 */
class AlwaysTruePrerequisite : Prerequisite {
    override fun check(): Boolean {
        return true
    }

    override fun reason(): String {
        return "This prerequisite is always true"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AlwaysTruePrerequisite) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}
