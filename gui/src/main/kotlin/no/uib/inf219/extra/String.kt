package no.uib.inf219.extra

/**
 * Remove the given substring from this string
 * @author Elg
 */
fun String.remove(old: String): String {
    return this.replace(old, "")
}

/**
 * Remove all newline characters `\n` and `\r`
 */
fun String.removeNl(): String {
    return this.remove("\n").remove("\r")
}
