package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.CollectionLikeType
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.backend.primitive.*
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ClassSelectorView
import tornadofx.*


/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * @author Elg
 */
interface ClassBuilder<out T> {

    val type: JavaType

    /**
     * The parent class builder, `null` if this is the root class builder or unknown parent
     */
    val parent: ClassBuilder<*>?

    /**
     * Key of the property to access this from the parent (if any)
     */
    val name: String

    /**
     * The property this class builder is creating, used for gaining additional metadata about what we're creating.
     */
    val property: PropertyWriter?

    /**
     * Convert this class builder to a json node
     */
    fun toTree(): JsonNode

    /**
     * Convert this object to an instance of [T]
     */
    fun toObject(): T?

    /**
     * return all sub values this class can hold. The [Map.keys] must exactly return all valid keys. If any of them have a default value they must reflect so
     *
     * Empty if [isLeaf] is true
     */
    fun getSubClassBuilders(): Map<String, ClassBuilder<*>?>

    /**
     * If this implementation does not have any sub class builders
     */
    fun isLeaf(): Boolean

    /**
     * Visual representation (and possibly modification) of this class builder
     */
    fun toView(parent: EventTarget, controller: ObjectEditorController): Node

    /**
     * Note that this will a
     *
     * @return A class builder for the given property, or `null` for if [isLeaf] is `true`
     * @throws IllegalArgumentException If the given [property] is not valid
     */
    fun createClassBuilderFor(property: String): ClassBuilder<*>?


    /**
     * Reset the given property for the [property] provided. If it has a default value this value will be restored
     *
     * @return If all referenced should be null-ed out
     */
    fun reset(property: String): ClassBuilder<*>? {
        return reset(property, null)
    }

    /**
     * Reset the given property for the [property] provided. If it has a default value this value will be restored
     *
     *
     * @return The new (potentially null) classbuilder, might be equal to [element]
     *
     * @throws IllegalArgumentException If both [property] and [element] are `null`
     */
    fun reset(property: String, element: ClassBuilder<*>?): ClassBuilder<*>?

    /**
     * Preview of this class
     */
    fun previewValue(): String

    fun getClassBuilder(
        type: JavaType,
        name: String,
        value: Any? = null,
        prop: PropertyWriter? = null
    ): ClassBuilder<*>? {
        return getClassBuilder(type, name, this, value, prop)
    }

    /**
     * @return If we are a forefather of the given [ClassBuilder]
     */
    fun isParent(to: ClassBuilder<*>?): Boolean {
        return when (to) {
            null -> false
            this -> true
            else -> this.isParent(to.parent)
        }
    }

    /**
     * Mark this object as dirt to flush [toObject] cache
     */
    fun isRequired(): Boolean {

    /////////////////////////////////////

    fun childeren()

    /**
     * If this class builder is required to be valid. If [property] is `null` this is assumed to be required.
     */
    fun isRequired(): Boolean {
        return property?.isRequired ?: true
    }

    companion object {

        /**
         * Get a correct class builder for the given java type
         */
        fun getClassBuilder(
            type: JavaType,
            name: String,
            parent: ClassBuilder<*>? = null,
            value: Any? = null,
            prop: PropertyWriter? = null,
            superType: JavaType = type
        ): ClassBuilder<*>? {
            return if (value != null && value is ClassBuilder<*>) {
                //it would be very weird if this happened
                require(value.type == type) { "The value given is a already a class builder, but its type (${value.type}) does not match with the given java type $type" }
                return value
            } else if (type.isPrimitive) {
                when {
                    type.isTypeOrSuperTypeOf(Byte::class.java) -> {
                        if (value == null) ByteClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            ByteClassBuilder(
                                value as Byte,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Short::class.java) -> {
                        if (value == null) ShortClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            ShortClassBuilder(
                                value as Short,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Int::class.java) -> {
                        if (value == null) IntClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            IntClassBuilder(
                                value as Int,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Long::class.java) -> {
                        if (value == null) LongClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            LongClassBuilder(
                                value as Long,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Float::class.java) -> {
                        if (value == null) FloatClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            FloatClassBuilder(
                                value as Float,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Double::class.java) -> {
                        if (value == null) DoubleClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            DoubleClassBuilder(
                                value as Double,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Char::class.java) -> {
                        if (value == null) CharClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            CharClassBuilder(
                                value as Char,
                                name,
                                parent,
                                prop
                            )
                    }
                    type.isTypeOrSuperTypeOf(Boolean::class.java) -> {
                        if (value == null) BooleanClassBuilder(
                            name = name,
                            parent = parent,
                            prop = prop
                        ) else
                            BooleanClassBuilder(
                                value as Boolean,
                                name,
                                parent,
                                prop
                            )
                    }
                    else -> throw IllegalStateException("Unknown primitive $type")
                }
            } else if (type.isTypeOrSuperTypeOf(String::class.java)) {
                //Strings is not a primitive, but its not far off
                if (value == null) StringClassBuilder(
                    name = name,
                    parent = parent,
                    prop = prop
                ) else
                    StringClassBuilder(
                        value as String,
                        name,
                        parent,
                        prop
                    )
            } else if (type.isCollectionLikeType && (type as CollectionLikeType).isTrueCollectionType) {
                //TODO add support for non-true collection types
                CollectionClassBuilder<Any>(type, name, parent, prop)
            } else if (type.isMapLikeType && (type as MapLikeType).isTrueMapType) {
                //TODO add support for non-true map types
                MapClassBuilder<Any, Any>(type, name, parent, prop)

            } else if (type.isArrayType) {
                TODO("Arrays not yet supported")
            } else if (type.isEnumType) {
                TODO("Enums not yet supported")
            } else if (type.rawClass.isAnnotation) {
                TODO("Handle annotation")

            } else if (!type.isConcrete) {
                //the type is abstract/interface we need a concrete type to
                val subtype = find<ClassSelectorView>().subtypeOf(type, false) ?: return null
                return getClassBuilder(subtype, name, parent, value, prop, superType)
            } else {
                //it's not a primitive type so let's just make a complex type for it
                ComplexClassBuilder<Any>(type, name, parent, prop, superType)
            }
        }
    }
}

