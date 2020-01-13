package no.uib.inf219.example.data.prerequisite

import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * @author Elg
 */
interface Prerequisite : ConfigurationSerializable {

    fun check(): Boolean

    fun reason(): String
}
