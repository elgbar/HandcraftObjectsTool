package no.uib.inf219.example.data.prerequisite

/**
 * @author Elg
 */
class AndPrerequisite : Prerequisite {


    lateinit var others: List<Prerequisite>

    companion object {
        const val OTHERS_PATH = "others"

        @JvmStatic
        fun deserialize(map: Map<String, Any?>): AndPrerequisite {
            val responses = ArrayList<Prerequisite>()
            for ((i, elem) in (map[OTHERS_PATH] as List<Any?>).withIndex()) {
                when (elem) {
                    is Prerequisite -> {
                        responses += elem
                    }
                    else -> {
                        throw IllegalStateException("Given map contains non-prerequisites. Found a ${if (elem == null) "null" else elem::class.java.simpleName} at position $i")
                    }
                }
            }

            val a = AndPrerequisite()
            a.others = responses
            return a
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

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        map[OTHERS_PATH] = others
        return map
    }
}
