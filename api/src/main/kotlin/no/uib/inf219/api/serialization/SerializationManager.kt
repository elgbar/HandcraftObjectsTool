package no.uib.inf219.api.serialization

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule


/**
 * @author Elg
 */
object SerializationManager {

    //    var mapper = ObjectMapper(YAMLFactory())
    var mapper = ObjectMapper()

    init {
        mapper.findAndRegisterModules()
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
//        mapper.activateDefaultTyping(
//            LaissezFaireSubTypeValidator.instance,
//            ObjectMapper.DefaultTyping.EVERYTHING,
//            JsonTypeInfo.As.WRAPPER_ARRAY
//        )
        mapper.registerModule(
            KotlinModule(
                nullisSameAsDefault = true,
                nullToEmptyCollection = true
            )
        )
    }

    @JvmStatic
    fun registerConfigurationSerializers(packagePath: String) {
//        ClassGraph().whitelistPackages(packagePath).scan().use { scanResult ->
//            registerInterface(HotSerializer::class.java, scanResult, ::registerRepresent)
//            registerInterface(HotDeserializer::class.java, scanResult, ::registerConstruct)
//        }
    }

//    private inline fun <T> registerInterface(
//        clazz: Class<T>,
//        scanResult: ScanResult,
//        regfun: (clazz: Class<*>, inst: T) -> Unit
//    ) {
//        val impls: List<Class<out T>> =
//            scanResult.getClassesImplementing(clazz.name).loadClasses(clazz, false)
//
//        for (subClass in impls) {
//            val serializableClazz: Class<*> =
//                subClass.getAnnotation(SerializerOf::class.java)?.value?.java ?: subClass
//            try {
//                val ser: T = subClass.getConstructor().newInstance()
//                regfun(subClass, ser)
//            } catch (e: Exception) {
//                throw SerializerException("Failed to register deserializer class ${serializableClazz.name} with ${subClass.name}")
//            }
//        }
//
//        println("deserImpl = ${impls.map { it.name }}")
//    }

    /**
     * Convert the given object to YAML
     *
     * @return The given object as represented by YAML
     */
    @JvmStatic
    fun dump(obj: Any): String {
        return mapper.writeValueAsString(obj)
    }

    /**
     * Load an object from YAML
     *
     * @return An instance of [T] with the properties of the given YAML
     */
    @JvmStatic
    inline fun <reified T> load(str: String): T {
        return mapper.readValue(str, T::class.java)
    }
}
