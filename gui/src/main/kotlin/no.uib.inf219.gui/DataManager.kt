package no.uib.inf219.gui

import no.uib.inf219.gui.components.AttributeComp
import no.uib.inf219.gui.components.PartComp
import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import java.io.File


/**
 * @author Elg
 */
object DataManager {

    private var logger = LoggerFactory.getLogger(DataManager::class.java)

    val PARTS: MutableMap<String, PartComp> = HashMap()

    fun findChildren(part: PartComp): Iterable<Pair<PartComp?, AttributeComp?>> {
        val childParts = ArrayList<Pair<PartComp?, AttributeComp?>>()

        for (attribute in part.attributes) {
            childParts.add(Pair(PARTS[attribute.className], attribute))
        }

        return childParts.asIterable()
    }

    fun addSource(file: File) {

    }

    private fun addSource(conf: Yaml, absPath: String = "") {
//        conf.addImplicitResolver()
//        for (type in conf.getKeys(true)) {
//            when (val sec = conf.get(type)) {
//                is PartData -> PARTS[sec.className] =
//                    PartComp(sec)
//                is ConfigurationSection -> addSource(
//                    sec,
//                    "$absPath.$type"
//                )
//                else -> System.err.println("Failed to find a config sec or data!")
//            }
//        }
        logger.info("data = $PARTS")
    }
}
