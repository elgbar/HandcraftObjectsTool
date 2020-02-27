package no.uib.inf219.gui.backend.exceptions

import com.fasterxml.jackson.databind.JavaType

/**
 * Exception for when a required property is not available
 * @author Elg
 */
class MissingPropertyException(prop: String, propType: JavaType, parentType: JavaType, e: Throwable? = null) :
    RuntimeException("Failed to create $parentType as the required property $prop ($propType) is null", e) {
}
