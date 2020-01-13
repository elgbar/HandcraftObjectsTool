package no.uib.inf219.api.serialization

import io.github.classgraph.ClassGraph
import no.kh498.util.ConfigUtil
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization

/**
 * @author Elg
 */
object SerializationManager {

    private val yaml = YamlConfiguration()

    @JvmStatic
    fun registerConfigurationSerializers(packagePath: String) {
        ClassGraph()
            .whitelistPackages(packagePath).scan().use { scanResult ->

                val subTypes: List<Class<out ConfigurationSerializable>> =
                    scanResult.getClassesImplementing(ConfigurationSerializable::class.java.name)
                        .loadClasses(ConfigurationSerializable::class.java, false)
//                println("scanResult = ${scanResult.allClasses.map { it.name }}")
//                println("subTypes = ${subTypes.map { it.name }}")
                for (subType in subTypes) {
                    ConfigurationSerialization.registerClass(subType)
                }
            }
    }

    @JvmStatic
    fun dump(obj: Any): String {
        return yaml.yaml.dumpAsMap(obj)
    }


    @JvmStatic
    fun <T> load(str: String): T {


        //let snake yaml convert the string to map
        val conf = YamlConfiguration()

        try {
            return conf.yaml.load<T>(str)
        } catch (e: InvalidConfigurationException) {

            conf.load(str)
            val map = ConfigUtil.getMapSection(conf.getKeys(true))

            //then use its de
            return ConfigurationSerialization.deserializeObject(map) as T
        }
    }
}
