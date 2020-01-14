package no.uib.inf219.example.data.prerequisite.logical

/**
 * @author Elg
 */
class OrPrerequisite : LogicalPrerequisite() {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any?>): OrPrerequisite {
            return deserializeOthers(OrPrerequisite(), map)
        }
    }

    override fun check(): Boolean {
        return others.any {
            it.check()
        }
    }

    override fun reason(): String {
        return "No prerequisite are true"
    }
}
