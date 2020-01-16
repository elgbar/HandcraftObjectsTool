package no.uib.inf219.example.data.prerequisite.logical

import no.uib.inf219.example.data.prerequisite.Prerequisite

/**
 * @author Elg
 */
class AndPrerequisite(others: List<Prerequisite>) : LogicalPrerequisite(others) {

    override fun check(): Boolean {
        return others.all {
            it.check()
        }
    }

    override fun reason(): String {
        return "One or more of the given prerequisite are false"
    }
}
