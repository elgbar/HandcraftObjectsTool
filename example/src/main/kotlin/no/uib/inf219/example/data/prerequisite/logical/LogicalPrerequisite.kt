package no.uib.inf219.example.data.prerequisite.logical

import no.uib.inf219.example.data.prerequisite.Prerequisite

/**
 * @author Elg
 */
abstract class LogicalPrerequisite : Prerequisite {

    lateinit var others: List<Prerequisite>

    companion object {
        const val OTHERS_PATH = "others"

        @JvmStatic
        fun <T : LogicalPrerequisite> deserializeOthers(
            impl: T,
            map: Map<String, Any?>
        ): T {
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
            impl.others = responses
            return impl
        }
    }

    override fun serialize(): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        if (!::others.isInitialized) throw IllegalStateException("Cannot serialize an object that is not initialized")
        map[OTHERS_PATH] = others
        return map
    }

}
