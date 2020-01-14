package no.uib.inf219.api.serialization

import io.github.classgraph.ClassGraph
import no.kh498.util.ConfigUtil
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerialization

/**
 * @author Elg
 */
object SerializationManager {


    /**
     * Register a [Serializable] class so it can be used when serializing
     *
     * @see registerConfigurationSerializers to register multiple classes at once
     */
    @JvmStatic
    fun registerClass(clazz: Class<out Serializable>) {
        ConfigurationSerialization.registerClass(clazz)
    }

    /**
     * Register all classes that extend [Serializable]
     *
     * @see registerClass to register a specific class
     */
    @JvmStatic
    fun registerConfigurationSerializers(packagePath: String) {
        ClassGraph().whitelistPackages(packagePath).scan().use { scanResult ->

            val subTypes: List<Class<out Serializable>> =
                scanResult.getClassesImplementing(Serializable::class.java.name)
                    .loadClasses(Serializable::class.java, false)

            for (subType in subTypes) {
                registerClass(subType)
            }
        }
    }

    /**
     * Convert the given object to YAML
     *
     * @return The given object as represented by YAML
     */
    @JvmStatic
    fun dump(obj: Any): String {
        return YamlConfiguration().yaml.dumpAsMap(obj)
    }

    /**
     * Load an object from YAML
     *
     * @return An instance of [T] with the properties of the given YAML
     */
    @JvmStatic
    fun <T> load(str: String): T {
        val conf = YamlConfiguration()

        return try {
            conf.yaml.load<T>(str)
        } catch (e: InvalidConfigurationException) {

            conf.load(str)
            val map = ConfigUtil.getMapSection(conf.getKeys(true))

            //then use its de
            ConfigurationSerialization.deserializeObject(map) as T
        }
    }
}
