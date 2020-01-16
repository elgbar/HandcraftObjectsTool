package no.uib.inf219.example.data.prerequisite.logical

import no.uib.inf219.example.data.prerequisite.Prerequisite

/**
 * @author Elg
 */
class OrPrerequisite(others: List<Prerequisite>) : LogicalPrerequisite(others) {

    override fun check(): Boolean {
        return others.any {
            it.check()
        }
    }

    override fun reason(): String {
        return "No prerequisite are true"
    }
}
