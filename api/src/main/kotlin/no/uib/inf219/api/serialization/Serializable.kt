package no.uib.inf219.api.serialization

import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.SerializableAs

/**
 * Represents an object that may be serialized.
 *
 *
 * These objects MUST implement one of the following, in addition to the
 * methods as defined by this interface:
 *
 *  * A static method "deserialize" that accepts a single [Map]&lt;
 * [String], [Object]&gt; and returns an instance of the class.
 *  * A static method "valueOf" that accepts a single [Map]&lt;[ ], [Object]&gt; and returns an instance of  the class.
 *  * A constructor that accepts a single [Map]&lt;[String],
 * [Object]&gt;.
 *
 * In addition to implementing this interface, you must register the class
 * with [SerializationManager.registerClass] or all in a package with[SerializationManager.registerConfigurationSerializers].
 *
 * @see SerializationManager
 * @see SerializableAs
 */
interface Serializable : ConfigurationSerializable {

    /**
     * Creates a Map representation of this class.
     *
     *
     * This class must provide a method to restore this class
     *
     * @return Map containing the current state of this class
     */
    override fun serialize(): Map<String, Any?>

    //If you using kotlin make sure to annotate the method with @JvmStatic
    //Below are the signature for the different deserialization methods kotlin and java
    //The `T` below should be replaced with the actual class
    //Remember you only need to implement one

    //kotlin

//    companion object {
//
//        @JvmStatic
//        fun <T> deserialize(map: Map<String, Any?>): Serializable { }
//
//        @JvmStatic
//        fun <T> valueOf(map: Map<String, Any?>): Serializable { }
//    }

    //java

//    public static T deserialize(Map<String, ?> map){ }

//    public static T valueOf(Map<String, ?> map){ }
}
