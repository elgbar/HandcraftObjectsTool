package no.uib.inf219.gui

import no.uib.inf219.gui.components.AttributeComp
import no.uib.inf219.gui.components.PartComp
import org.slf4j.LoggerFactory
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
//        ConfigurationSerialization.registerClass(PartData::class.java)
//        ConfigurationSerialization.registerClass(AttributeData::class.java)
//        val root = Yaml()
////        root.options().pathSeparator(':')
//
//
//        try {
//            root.load(file)
//        } catch (ex: FileNotFoundException) {
//        } catch (ex: IOException) {
//            println("Cannot load $file\n$ex")
//        } catch (ex: InvalidConfigurationException) {
//            println("Cannot load $file\n$ex")
//        }
//        System.err.println("root.saveToString() = ${root.saveToString()}")
//
//        addSource(root)
    }

//    private fun addSource(conf: ConfigurationSection, absPath: String = "") {
//        for (type in conf.getKeys(true)) {
//            when (val sec = conf.get(type)) {
//                is PartData -> PARTS[sec.className] = PartComp(sec)
//                is ConfigurationSection -> addSource(sec, "$absPath.$type")
//                else -> System.err.println("Failed to find a config sec or data!")
//            }
//        }
//        println("data = $PARTS")
//    }
}
