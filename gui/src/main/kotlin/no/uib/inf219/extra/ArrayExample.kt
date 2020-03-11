package no.uib.inf219.extra

/**
 * @author Elg
 */
data class ArrayExample(val pre: Array<String>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArrayExample) return false

        if (!pre.contentEquals(other.pre)) return false

        return true
    }

    override fun hashCode(): Int {
        return pre.contentHashCode()
    }
}
