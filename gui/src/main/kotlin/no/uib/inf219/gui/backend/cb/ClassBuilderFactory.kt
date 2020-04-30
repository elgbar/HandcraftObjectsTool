package no.uib.inf219.gui.backend.cb

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.MapLikeType
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.parents.CollectionClassBuilder
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.parents.MapClassBuilder
import no.uib.inf219.gui.backend.cb.simple.*
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.EmptyClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.gui.view.select.ClassSelectorView
import tornadofx.find
import tornadofx.information
import tornadofx.warning
import java.util.*
import kotlin.reflect.full.isSuperclassOf


/**
 * Get a correct class builder for the given [type]. This method is preferred over calling the any constructor directly as this handle the set up the given item
 */
fun createClassBuilder(
    type: JavaType,
    key: ClassBuilder,
    parent: ParentClassBuilder,
    value: Any? = null,
    prop: ClassInformation.PropertyMetadata? = parent.getChildPropertyMetadata(key),
    item: TreeItem<ClassBuilderNode> = TreeItem(),
    allowAbstractType: Boolean = false
): ClassBuilder? {

    require(!parent.isLeaf()) { "Parent cannot be a leaf" }
    require(item !== parent.item) { "Cyclic dependencies not allowed for items" }

    if (value != null) {
        if (value::class.javaPrimitiveType != null) {
            val primGivenType = value::class.javaPrimitiveType
            val primExpectedType = type.rawClass.kotlin.javaPrimitiveType
            require(primExpectedType == primGivenType) {
                "Mismatch between given java class and expected class. The given value can be reduced to the primitive type $primGivenType while the primitive of internal type is $primExpectedType (originally $type)"
            }
        } else {
            val givenClass = value::class.java
            require(type.isTypeOrSuperTypeOf(givenClass)) {
                "Mismatch between given java class and expected class. The given value has the class '$givenClass' while the expected type is $type"
            }
        }
    }

    val cb = (if (type.rawClass.kotlin.javaPrimitiveType != null) {
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
        CollectionClassBuilder(
            type,
            key = key,
            parent = parent,
            property = prop,
            item = item
        )
    } else if (type.isMapLikeType && (type as MapLikeType).isTrueMapType) {
        //TODO add support for non-true map types
        MapClassBuilder(
            type,
            key = key,
            parent = parent,
            property = prop,
            item = item
        )
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
            if (ControlPanelView.mrBeanModuleEnabled) {
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

        if (ControlPanelView.mrBeanModuleEnabled) {
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
        if (ControlPanelView.mrBeanModuleEnabled && !allowAbstractNextTime) {
            displayWarning()
        }

        createClassBuilder(
            subtype,
            key,
            parent,
            value,
            prop,
            item,
            allowAbstractNextTime
        )
    } else {

        //it's not a primitive type so let's just make a complex type for it
        ComplexClassBuilder(
            type,
            key = key,
            parent = parent,
            property = prop,
            item = item
        )
    }) ?: return null

    require(cb.item === item)

    item.value =
        FilledClassBuilderNode(key, cb, parent, allowReference = cb.item.value?.allowReference ?: true)


    if (cb is ParentClassBuilder) {
        item.children.setAll(cb.getChildren().map { (key, childCb) ->
            //use the existing node or create an empty node if the child is null
            childCb?.node ?: EmptyClassBuilderNode(key, cb)
        }.map { it.item })
    }
    return cb
}
