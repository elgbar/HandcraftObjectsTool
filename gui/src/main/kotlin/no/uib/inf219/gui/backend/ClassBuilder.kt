package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.beans.Observable
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.gui.backend.serializers.ClassBuilderSerializer
import no.uib.inf219.gui.backend.simple.*
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation.PropertyMetadata
import no.uib.inf219.gui.view.ClassSelectorView
import no.uib.inf219.gui.view.ControlPanelView
import tornadofx.find
import tornadofx.property
import tornadofx.warning
import java.util.*
import kotlin.collections.ArrayList

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * TODO extract methods that should only be available for class builder that has children (ie NOT simple cb)
 *
 * @author Elg
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator::class)
@JsonSerialize(using = ClassBuilderSerializer::class)
interface ClassBuilder<out T> {

    /**
     * The object to serialize
     */
    val serObject: Any

    /**
     * Observable property for [serObject]
     */
    val serObjectObservable: Observable

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
    val key: ClassBuilder<*>?

    /**
     * The property this class builder is creating, used for gaining additional metadata about what we're creating.
     */
    @get:JsonIgnore
    val property: PropertyMetadata?

    /**
     * Convert this object to an instance of [T].
     * The returned object must not change unless there are changes further down the class builder change
     */
    fun toObject(): T? {
        return ControlPanelView.mapper.convertValue<T>(this, type)
    }

    /**
     *
     * @return all sub values this class can hold. The [Map.keys] must exactly return all valid keys.
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
     * If this is an end to the class builder tree. Usually this means that [getSubClassBuilders] is empty, but it is not guaranteed.
     *
     * @see ReferenceClassBuilder Is a leaf, but [getSubClassBuilders] is not empty
     */
    @JsonIgnore
    fun isLeaf(): Boolean

    /**
     * Visual representation (and possibly modification) of this class builder
     */
    fun toView(parent: EventTarget, controller: ObjectEditorController): Node

    /**
     *
     * @param key The key to the property to create. All keys from [getSubClassBuilders] are guaranteed to work, others might work but it is up to the implementation to accept or reject keys
     * @param init The value to be placed at the given [key] property
     *
     * @return A class builder for the given property type found at [key].
     *
     *
     * @throws IllegalArgumentException If the given [key] is not valid or [init] is invalid
     */
    fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>? = null): ClassBuilder<*>?

    /**
     * Reset the given property for the [key] provided. If it has a default value this value will be restored otherwise it will be removed.
     *
     * @param key Specify which child class builder to reset
     * @param element The instance of the child to reset. Must be identical to the class builder found with [key] or be `null`
     * @param restoreDefault If the default value (if none default is `null`) should be restored. If `false` the child found at [key] will be `null` after this method
     *
     * @throws IllegalArgumentException If child found with [key] does not match [element]. Will not be thrown if [element] is `null`
     */
    fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>? = null, restoreDefault: Boolean = true)

    /**
     * Preview of the value of this class builder
     */
    @JsonIgnore
    fun getPreviewValue(): String

    fun getClassBuilder(
        type: JavaType,
        key: ClassBuilder<*>?,
        value: Any? = null,
        prop: PropertyMetadata? = null
    ): ClassBuilder<*>? {
        return getClassBuilder(type, key, this, value, prop)
    }

    /**
     * @return If this is a forefather of the given [ClassBuilder]. Will return `false` if `this` is equal to [to]
     */
    fun isParentOf(to: ClassBuilder<*>?): Boolean {
        if (to == null) return false
        return when (to.parent) {
            null -> false
            this -> true
            else -> this.isParentOf(to.parent)
        }
    }

    /**
     * @return The java type of of the given child
     */
    fun getChildType(cb: ClassBuilder<*>): JavaType?


    /**
     * @return The child at the given location
     *
     * @throws IllegalArgumentException If the [key] is invalid
     */
    fun getChild(key: ClassBuilder<*>): ClassBuilder<*>?

    /**
     * If this class builder is required to be valid. If [property] is `null` this is assumed to be required.
     */
    @JsonIgnore
    fun isRequired(): Boolean {
        return property?.required ?: false
    }

    /**
     * @return `true` if this class builder cannot change value
     */
    @JsonIgnore
    fun isImmutable(): Boolean

    companion object {

        /**
         * Get a correct class builder for the given java type.
         * This is a convenience method to not deal with types when the type is unknown
         */
        fun getClassBuilder(
            type: JavaType,
            key: ClassBuilder<*>? = null,
            parent: ClassBuilder<*>? = null,
            prop: PropertyMetadata? = null
        ): ClassBuilder<Any>? {
            return getClassBuilder<Any>(type, key, parent, null, prop)
        }

        /**
         * Get a correct class builder for the given java type.
         * We do not return `ClassBuilder<T>` as some class use more advanced types such as `Collection<T>` and `Map<K,V>`
         *
         * The given type overrules the method type
         *
         */
        fun <T : Any> getClassBuilder(
            type: JavaType,
            key: ClassBuilder<*>? = null,
            parent: ClassBuilder<*>? = null,
            value: T? = null,
            prop: PropertyMetadata? = null
        ): ClassBuilder<Any>? {

            require(parent == null || !parent.isLeaf()) { "Parent cannot be a leaf" }

            if (value != null) {
                val clazz: Class<*> = if (type.isPrimitive) value::class.javaPrimitiveType!! else value::class.java
                require((type.rawClass == clazz) || type.rawClass.isAssignableFrom(clazz)) {
                    "Mismatch between given java type and the initial value. Given java type $type, initial value type $clazz"
                }
            }

            return if (type.isPrimitive) {
                when {
                    type.isTypeOrSuperTypeOf(Int::class.java) -> {
                        if (value == null) IntClassBuilder(key = key, parent = parent, prop = prop) else
                            IntClassBuilder(value as Int, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Long::class.java) -> {
                        if (value == null) LongClassBuilder(name = key, parent = parent, prop = prop) else
                            LongClassBuilder(value as Long, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Float::class.java) -> {
                        if (value == null) FloatClassBuilder(name = key, parent = parent, prop = prop) else
                            FloatClassBuilder(value as Float, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Double::class.java) -> {
                        if (value == null) DoubleClassBuilder(name = key, parent = parent, prop = prop) else
                            DoubleClassBuilder(value as Double, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Boolean::class.java) -> {
                        if (value == null) BooleanClassBuilder(name = key, parent = parent, prop = prop) else
                            BooleanClassBuilder(value as Boolean, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Char::class.java) -> {
                        if (value == null) CharClassBuilder(name = key, parent = parent, prop = prop) else
                            CharClassBuilder(value as Char, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Byte::class.java) -> {
                        if (value == null) ByteClassBuilder(name = key, parent = parent, prop = prop) else
                            ByteClassBuilder(value as Byte, key, parent, prop)
                    }
                    type.isTypeOrSuperTypeOf(Short::class.java) -> {
                        if (value == null) ShortClassBuilder(name = key, parent = parent, prop = prop) else
                            ShortClassBuilder(value as Short, key, parent, prop)
                    }
                    else -> throw IllegalStateException("Unknown primitive $type")
                }
            } else if (type.isTypeOrSuperTypeOf(String::class.java)) {
                //Strings is not a primitive, but its not far off
                val init = if (value != null) value as String else ""
                StringClassBuilder(init, key, parent, prop)
            } else if (type.isTypeOrSuperTypeOf(UUID::class.java)) {
                UUIDClassBuilder(UUID.randomUUID(), key, parent, prop)
            } else if (type.isCollectionLikeType || type.isArrayType) {
                CollectionClassBuilder<T>(type, key, parent, prop)
            } else if (type.isMapLikeType && (type as MapLikeType).isTrueMapType) {
                //TODO add support for non-true map types
                MapClassBuilder<Any, T>(type, key, parent, prop)
            } else if (type.isEnumType) {
                @Suppress("UNCHECKED_CAST") //checking with isEnumType above
                val enumClass = type.rawClass as Class<Enum<*>>
                EnumClassBuilder(enumClass, enumClass.cast(value), key, parent, prop)

            } else if (type.rawClass.isAnnotation) {
                error("Serialization of annotations is not supported, is there even any way to serialize them?")
            } else if (!type.isConcrete) {
                //the type is abstract/interface we need a concrete type to
                val subtype = find<ClassSelectorView>().subtypeOf(type, true) ?: return null

                if (ControlPanelView.useMrBean) {
                    if (!subtype.rawClass.isAnnotationPresent(JsonTypeInfo::class.java)) {
                        return ComplexClassBuilder(subtype, key, parent, prop)
                    } else {
                        warning(
                            "Polymorphic types with type information not allowed with MrBean module",
                            "Since base classes are often abstract classes, but those classes should not be materialized, because they are never used (instead, actual concrete sub-classes are used). Because of this, Mr Bean will not materialize any types annotated with @JsonTypeInfo annotation."
                        )
                        return null
                    }
                }
                getClassBuilder(subtype, key, parent, value, prop)

            } else {

                //it's not a primitive type so let's just make a complex type for it
                ComplexClassBuilder(type, key, parent, prop)
            }
        }
    }
}
