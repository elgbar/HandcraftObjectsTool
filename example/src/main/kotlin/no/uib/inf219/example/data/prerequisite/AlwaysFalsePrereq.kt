package no.uib.inf219.example.data.prerequisite

/**
 * @author Elg
 */
class AlwaysFalsePrereq : Prerequisite {
    override fun check(): Boolean {
        return false
    }

    override fun reason(): String {
        return "This prerequisite is always false"
    }

    override fun serialize(): Map<String, Any?> {
        return HashMap()
    }

    companion object {
        
        @JvmStatic
        fun deserialize(map: Map<String, Any?>): AlwaysFalsePrereq {
            return AlwaysFalsePrereq()
        }
    }

}
