package no.uib.inf219.example.data.showcase

import com.fasterxml.jackson.annotation.JsonValue
import java.util.*

/**
 * @author Elg
 */
class JsonValueExample {

    @JsonValue
    var uid: UUID = UUID.randomUUID()
}
