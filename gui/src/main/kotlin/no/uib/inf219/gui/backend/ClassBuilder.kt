package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.beans.Observable
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.input.MouseEvent
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.backend.serializers.ClassBuilderSerializer
import no.uib.inf219.gui.backend.simple.*
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.controllers.classBuilderNode.EmptyClassBuilderNode
import no.uib.inf219.gui.controllers.classBuilderNode.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.loader.ClassInformation.PropertyMetadata
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.ControlPanelView.mrBeanModuleEnabled
import no.uib.inf219.gui.view.select.ClassSelectorView
import tornadofx.find
import tornadofx.information
import tornadofx.warning
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.reflect.full.isSuperclassOf

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
interface ClassBuilder {

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
     * The parent class builder. If root parent is `this`
     */
    @get:JsonIgnore
    val parent: ParentClassBuilder

    /**
     * Key of the property to access this from the parent
     */
    @get:JsonIgnore
    val key: ClassBuilder

    /**
     * The property this class builder is creating, used for gaining additional metadata about what we're creating.
     */
    @get:JsonIgnore
    val property: PropertyMetadata?

    /**
     * The tree item representing this class builder
     */
    @get:JsonIgnore
    val item: TreeItem<ClassBuilderNode>
        get() = parent.item.findChild(key)

    val node: FilledClassBuilderNode get() = item.value as FilledClassBuilderNode

    /**
     * Convert this object to an instance of [type].
     * The returned object must not change unless there are changes further down the class builder change
     */
    fun toObject(): Any? {
        return ControlPanelView.mapper.convertValue(this, type)
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
    fun createEditView(parent: EventTarget, controller: ObjectEditorController): Node

    /**
     * Preview of the value of this class builder
     */
    @JsonIgnore
    fun getPreviewValue(): String

    /**
     * @return `true` if this class builder cannot change value
     */
    @JsonIgnore
    fun isImmutable(): Boolean

    /**
     * When the node of this class builder got a mouse event
     */
    fun onNodeClick(event: MouseEvent, controller: ObjectEditorController) {}

    /**
     * Allow class builder to customize what is displayed when right clicking on it's node.
     *
     * @return if a separator should be added before the items added here
     */
    fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean = false

    companion object {

        /**
         * Get a correct class builder for the given java type.
         * This is a convenience method to not deal with types when the type is unknown
         */
        fun createClassBuilder(
            type: JavaType,
            key: ClassBuilder,
            parent: ParentClassBuilder,
            prop: PropertyMetadata? = null,
            item: TreeItem<ClassBuilderNode> = TreeItem()
        ): ClassBuilder? {
            return createClassBuilder(type, key, parent, null, prop, item)
        }

        /**
         * Get a correct class builder for the given java type.
         * We do not return `ClassBuilder` as some class use more advanced types such as `Collection<T>` and `Map<K,V>`
         *
         * The given type overrules the method type
         *
         */
        fun <T : Any> createClassBuilder(
            type: JavaType,
            key: ClassBuilder,
            parent: ParentClassBuilder,
            value: T? = null,
            prop: PropertyMetadata? = null,
            item: TreeItem<ClassBuilderNode> = TreeItem(),
            allowAbstractType: Boolean = false
        ): ClassBuilder? {

            require(!parent.isLeaf()) { "Parent cannot be a leaf" }
            require(item !== parent.item) { "Cyclic dependencies not allowed for items" }

            if (value != null) {
                val clazz: Class<*> = if (type.isPrimitive) value::class.javaPrimitiveType!! else value::class.java
                require((type.rawClass == clazz) || type.rawClass.isAssignableFrom(clazz)) {
                    "Mismatch between given java type and the initial value. Given java type $type, initial value type $clazz"
                }
            }

            val cb = (if (type.isPrimitive) {
                val kotlinType = type.rawClass.kotlin
                when {
                    kotlinType.isSuperclassOf(Int::class) -> {
                        if (value == null) IntClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            IntClassBuilder(value as Int, key = key, parent = parent, property = prop, item = item)
                    }
                    kotlinType.isSuperclassOf(Long::class) -> {
                        if (value == null) LongClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            LongClassBuilder(value as Long, key = key, parent = parent, property = prop, item = item)
                    }
                    kotlinType.isSuperclassOf(Float::class) -> {
                        if (value == null) FloatClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            FloatClassBuilder(value as Float, key = key, parent = parent, property = prop, item = item)
                    }
                    kotlinType.isSuperclassOf(Double::class) -> {
                        if (value == null) DoubleClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            DoubleClassBuilder(
                                value as Double,
                                key = key,
                                parent = parent,
                                property = prop,
                                item = item
                            )
                    }
                    kotlinType.isSuperclassOf(Boolean::class) -> {
                        if (value == null) BooleanClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            BooleanClassBuilder(
                                value as Boolean,
                                key = key,
                                parent = parent,
                                property = prop,
                                item = item
                            )
                    }
                    kotlinType.isSuperclassOf(Char::class) -> {
                        if (value == null) CharClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            CharClassBuilder(value as Char, key = key, parent = parent, property = prop, item = item)
                    }
                    kotlinType.isSuperclassOf(Byte::class) -> {
                        if (value == null) ByteClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            ByteClassBuilder(value as Byte, key = key, parent = parent, property = prop, item = item)
                    }
                    kotlinType.isSuperclassOf(Short::class) -> {
                        if (value == null) ShortClassBuilder(
                            key = key,
                            parent = parent,
                            property = prop,
                            item = item
                        ) else
                            ShortClassBuilder(value as Short, key = key, parent = parent, property = prop, item = item)
                    }
                    else -> throw IllegalStateException("Unknown primitive $type")
                }

            } else if (type.isTypeOrSuperTypeOf(String::class.java)) {
                //Strings is not a primitive, but its not far off
                val init = if (value != null) value as String else ""
                StringClassBuilder(init, key = key, parent = parent, property = prop, item = item)
            } else if (type.isTypeOrSuperTypeOf(UUID::class.java)) {
                UUIDClassBuilder(UUID.randomUUID(), key = key, parent = parent, property = prop, item = item)
            } else if (type.isCollectionLikeType || type.isArrayType) {
                CollectionClassBuilder(type, key = key, parent = parent, property = prop, item = item)
            } else if (type.isMapLikeType && (type as MapLikeType).isTrueMapType) {
                //TODO add support for non-true map types
                MapClassBuilder(type, key = key, parent = parent, property = prop, item = item)
            } else if (type.isEnumType) {
                @Suppress("UNCHECKED_CAST") //checking with isEnumType above
                val enumClass = type.rawClass as Class<Enum<*>>
                EnumClassBuilder(
                    enumClass,
                    enumClass.cast(value),
                    key = key,
                    parent = parent,
                    property = prop,
                    item = item
                )

            } else if (type.rawClass.isAnnotation) {
                error("Serialization of annotations is not supported, is there even any way to serialize them?")
            } else if (!type.isConcrete && !allowAbstractType) {

                /**
                 * Nothing can be abstract if mr bean module is not enabled
                 * and only types that does not have type information can be abstract.
                 */
                fun canBeAbstract(type: JavaType): Boolean {
                    if (mrBeanModuleEnabled) {
                        val typeInfo = ClassInformation.serializableProperties(type)
                        return typeInfo.first == null
                    }
                    return false
                }

                fun displayWarning() {
                    warning(
                        "Polymorphic types with type information not allowed with MrBean module",
                        "Since base classes are often abstract classes, but those classes should not be materialized, because they are never used (instead, actual concrete sub-classes are used). Because of this, Mr Bean will not materialize any types annotated with @JsonTypeInfo annotation.\n" +
                                "Please select a sub class "
                    )
                }

                var allowAbstractNextTime = canBeAbstract(type)

                if (mrBeanModuleEnabled) {
                    //users might want to create the selected class not a subclass

                    val createThis = ButtonType("Create this", ButtonBar.ButtonData.OK_DONE)
                    val findSubclass = ButtonType("Find subclass", ButtonBar.ButtonData.NEXT_FORWARD)
                    information(
                        "You you want to create ${type.rawClass} or a sub class of it?",
                        "The class you want to create is an abstract class or an interface." +
                                "\nDo you want to create this abstract type or find a sub class of it?",
                        buttons = *arrayOf(createThis, findSubclass, ButtonType.CANCEL),
                        actionFn = {
                            when (it) {
                                ButtonType.CANCEL -> return null
                                createThis -> {
                                    if (!allowAbstractNextTime) {
                                        displayWarning()
                                    }
                                    return createClassBuilder(
                                        type,
                                        key,
                                        parent,
                                        value,
                                        prop,
                                        item,
                                        allowAbstractNextTime
                                    )
                                }
                            }
                        }
                    )
                }

                val subtype = find<ClassSelectorView>().subtypeOf(type, true) ?: return null

                allowAbstractNextTime = canBeAbstract(subtype)
                if (mrBeanModuleEnabled && !allowAbstractNextTime) {
                    displayWarning()
                }

                createClassBuilder(subtype, key, parent, value, prop, item, allowAbstractNextTime)
            } else {

                //it's not a primitive type so let's just make a complex type for it
                ComplexClassBuilder(type, key = key, parent = parent, property = prop, item = item)
            }) ?: return null

            require(cb.item == item)

            item.value = FilledClassBuilderNode(key, cb, parent, item)
            

            if (cb is ParentClassBuilder) {
                item.children.setAll(cb.getSubClassBuilders().map { (key, childCb) ->
                    //use the existing node or create an empty node if the child is null
                    childCb?.node ?: EmptyClassBuilderNode(key, cb)
                }.map { it.item })
            }
            return cb
        }
    }
}
