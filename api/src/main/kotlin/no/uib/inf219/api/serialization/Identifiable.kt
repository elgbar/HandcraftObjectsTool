package no.uib.inf219.api.serialization

import com.fasterxml.jackson.annotation.*

/**
 * @author Elg
 */
@JsonIdentityInfo(
    generator = ObjectIdGenerators.StringIdGenerator::class,
    scope = Identifiable::class,
    resolver = SimpleObjectIdResolver::class,
    property = "id"
)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
interface Identifiable<T> {

    /**
     * Unique id of this object
     */
    @JsonIgnore
    fun getId(): T
}
