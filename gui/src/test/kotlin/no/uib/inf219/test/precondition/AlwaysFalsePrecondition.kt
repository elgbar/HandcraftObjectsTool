package no.uib.inf219.test.precondition

/**
 * @author Elg
 */

class AlwaysFalsePrecondition : Precondition {
    override fun check(): Boolean {
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AlwaysFalsePrecondition) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }


}
