package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.type.CollectionLikeType
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.backend.primitive.*
import no.uib.inf219.gui.backend.serializers.ClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ClassSelectorView
import tornadofx.find
import tornadofx.property

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * @author Elg
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator::class)
//@JsonSerialize(using = ClassBuilderSerializer::class)
interface ClassBuilder<out T> {

    /**
     * The object to serialize
     */
    val serializationObject: Any

    @get:JsonIgnore
    val type: JavaType

    /**
     * The parent class builder, `null` if this is the root class builder or unknown parent
     */
    @get:JsonIgnore
    val parent: ClassBuilder<*>?

    /**
     * Key of the property to access this from the parent (if any)
     */
    @get:JsonIgnore
    val name: String

    /**
     * The property this class builder is creating, used for gaining additional metadata about what we're creating.
     */
    @get:JsonIgnore
    val property: PropertyWriter?


    fun mapToSerializableObject(cbs: ClassBuilderSerializer): Any

    /**
     * Convert this object to an instance of [T].
     * The returned object must not change unless there are changes further down the class builder change
     */
    fun toObject(): T?

    /**
     * return all sub values this class can hold. The [Map.keys] must exactly return all valid keys. If any of them have a default value they must reflect so
     *
     * Empty if [isLeaf] is true
     */
    @JsonIgnore
    fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?>

    /**
     * This is a subset of [getSubClassBuilders]
     *
     * @return all modifiable existing children
     */
    @JsonIgnore
    fun getChildren(): List<ClassBuilder<*>> {
        val list = ArrayList<ClassBuilder<*>>()
        for ((k, v) in getSubClassBuilders()) {
            if (!k.isImmutable()) list += k
            if (v != null && !v.isImmutable()) list += v
        }
        return list
    }

    /**
     * If this implementation does not have any sub class builders
     */
    @JsonIgnore
    fun isLeaf(): Boolean

    /**
     * Visual representation (and possibly modification) of this class builder
     */
    fun toView(parent: EventTarget, controller: ObjectEditorController): Node

    /**
     * Note that this will a
     *
     * @return A class builder for the given property, or `null` for if [isLeaf] is `true`
     * @throws IllegalArgumentException If the given [key] is not valid
     */
    fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>? = null): ClassBuilder<*>?

    /**
     * Reset the given property for the [cb] provided. If it has a default value this value will be restored
     *
     */
    fun resetChild(cb: ClassBuilder<*>) {
        return resetChild(cb, null)
    }

    /**
     * Reset the given property for the [key] provided. If it has a default value this value will be restored otherwise it will be removed.
     *
     */
    fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?)

    /**
     * Reset this class builder.
     *
     * @return `true` if this class builder should be removed from parent
     */
    fun reset(): Boolean

    /**
     * Preview of this class
     */
    @JsonIgnore
    fun getPreviewValue(): String

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
    fun recompile()

    @JsonIgnore
    fun isDirty(): Boolean

    /**
     * @return The java type of of the given child
     */
    fun getChildType(cb: ClassBuilder<*>): JavaType?

    /**
     * If this class builder is required to be valid. If [property] is `null` this is assumed to be required.
     */
    @JsonIgnore
    fun isRequired(): Boolean {
        return property?.isRequired ?: true
    }

    /**
     * @return `true` if this class builder cannot change value
     */
    @JsonIgnore
    fun isImmutable(): Boolean

    companion object {

        /**
         * Get a correct class builder for the given java type
         */
        fun getClassBuilder(
            type: JavaType,
            name: String,
            parent: ClassBuilder<*>? = null,
            value: Any? = null,
            prop: PropertyWriter? = null
        ): ClassBuilder<*>? {
            val elem = if (value != null && value is ClassBuilder<*>) {
                //it would be very weird if this happened
                require(value.type == type) {
                    "The value given is a already a class builder, but its type (${value.type}) does not match with the given java type $type"
                }
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
                return getClassBuilder(subtype, name, parent, value, prop)
            } else {
                //it's not a primitive type so let's just make a complex type for it
                ComplexClassBuilder<Any>(type, name, parent, prop)
            }
            //remember to recompile the parent to make sure it sees the changes
            parent?.recompile()
            return elem
        }
    }
}

