package no.uib.inf219.example.data.prerequisite

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

    override fun serialize(): Map<String, Any?> {
        return HashMap()
    }

    companion object {
        @JvmStatic
        fun deserialize(map: Map<String, Any?>): AlwaysTruePrerequisite {
            return AlwaysTruePrerequisite()
        }
    }
}
