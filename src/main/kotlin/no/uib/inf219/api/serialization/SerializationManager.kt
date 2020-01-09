package no.uib.inf219.api.serialization

/**
 * @author Elg
 */
object SerializationManager {
    @JvmStatic
    fun registerClass(clazz: Class<out Serializer>) {

    }

    val map: Map<Class<Any>, Serializer> = HashMap();


}
