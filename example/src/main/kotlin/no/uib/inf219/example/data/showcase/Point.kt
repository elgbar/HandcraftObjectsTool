package no.uib.inf219.example.data.showcase

/**
 * Taken from [https://github.com/FasterXML/jackson-modules-base/tree/master/mrbean#simple-usage](https://github.com/FasterXML/jackson-modules-base/tree/master/mrbean#simple-usage)
 */
interface Point {
    // may have setters and/or getters
    var x: Int

    // but setters are optional if getter exists:
    val y: Int
}
