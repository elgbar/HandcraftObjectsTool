package no.uib.inf219.example.data.prerequisite.logical

import com.fasterxml.jackson.annotation.JsonProperty
import no.uib.inf219.example.data.prerequisite.Prerequisite

/**
 * @author Elg
 */
abstract class LogicalPrerequisite(
    @JsonProperty("others", required = true)
//    @JsonSerialize(`as` = Prerequisite::class, typing = JsonSerialize.Typing.STATIC)
    val others: List<Prerequisite>
) : Prerequisite {

}
