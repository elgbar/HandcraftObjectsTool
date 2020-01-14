package no.uib.inf219.example.data.prerequisite.logical

/**
 * @author Elg
 */
class AndPrerequisite : LogicalPrerequisite() {

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any?>): AndPrerequisite {
            return deserializeOthers(AndPrerequisite(), map)
        }
    }

    override fun check(): Boolean {
        return others.all {
            it.check()
        }
    }

    override fun reason(): String {
        return "One or more of the given prerequisite are false"
    }
}
