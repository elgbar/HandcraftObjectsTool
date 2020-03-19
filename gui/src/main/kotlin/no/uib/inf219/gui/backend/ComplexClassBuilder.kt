package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.text.TextAlignment
import no.uib.inf219.extra.onChange
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.serializers.ComplexClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * A class builder intended to be used for normal classes. It is 'complex' due containing multiple other [ClassBuilder]s.
 *
 * @author Elg
 */
@JsonSerialize(using = ComplexClassBuilderSerializer::class)
class ComplexClassBuilder<out T>(
    override val type: JavaType,
    override val key: ClassBuilder<*>? = null,
    override val parent: ClassBuilder<*>? = null,
    override val property: ClassInformation.PropertyMetadata? = null
) : ClassBuilder<T> {

    /**
     * Hold information about the given property
     */
    internal val propInfo: Map<String, ClassInformation.PropertyMetadata>

    /**
     * Holds the default value for the given property
     */
    internal val propDefaults: MutableMap<String, Any?> = HashMap()

    /**
     * Information about the generics of [T], is `null` when the class does not have a generic type
     */
    val typeSerializer: TypeSerializer?

    val isJsonValueDelegator: Boolean

    override val serObject = HashMap<String, ClassBuilder<*>?>()
    override val serObjectObservable = serObject.asObservable()

    init {
        val (typeSer, pinfo, valueDelegator) = ClassInformation.serializableProperties(type)
        isJsonValueDelegator = valueDelegator
        typeSerializer = typeSer
        propInfo = pinfo

        //initiate all valid values to null or default
        // to allow for iteration when populating Node explorer
        for ((key, v) in propInfo) {
            propDefaults[key] = v.getDefaultInstance()
            if (propDefaults[key] != null || v.type.isPrimitive) {
                //only create a class builder for properties that has a default value
                // or is primitive (which always have default values)
                createClassBuilderFor(key.toCb())
            } else {
                this.serObject[key] = null
            }
        }
    }

    private fun cbToString(cb: ClassBuilder<*>?): String {
        return cb?.serObject as? String
            ?: kotlin.error("Wrong type of key was given. Expected a ClassBuilder<String> but got $cb")
    }

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? {
        val propName = cbToString(key)

        val prop = propInfo[propName]
        require(prop != null) { "The class $type does not have a property with the name '$propName'. Expected one of the following: ${propInfo.keys}" }
        require(init == null || init.type.isTypeOrSubTypeOf(getChildType(key)?.rawClass)) {
            "Given initial value have different type than expected. Expected a subclass of ${getChildType(key)} got ${init?.type}"
        }
        return serObjectObservable.computeIfAbsent(propName) {
            createChild(key, init, prop)
        }
    }

    override fun resetChild(
        key: ClassBuilder<*>,
        element: ClassBuilder<*>?,
        restoreDefault: Boolean
    ) {
        val propName = cbToString(key)

        require(serObject.containsKey(propName)) {
            "The class $type does not have a property with the name '$propName'. Expected one of the following: ${propInfo.keys}"
        }
        require(element == null || element === serObject[propName]) {
            "Given element to reset does not match with the internal element. element: $element, internal ${serObject[propName]}"
        }


        val newProp = if (restoreDefault && propDefaults[propName] != null) {
            val prop: ClassInformation.PropertyMetadata = propInfo[propName] ?: kotlin.error("Given prop name is wrong")
            //must be set to null to trigger the change event!
            // stupid javafx
            serObject[propName] = null //use non observable map to to trigger on change event
            createChild(key, null, prop)
        } else {
            null
        }
        serObjectObservable[propName] = newProp
    }

    private fun createChild(
        key: ClassBuilder<*>,
        init: ClassBuilder<*>?,
        prop: ClassInformation.PropertyMetadata
    ): ClassBuilder<*>? {
        return init ?: getClassBuilder(prop.type, key, propDefaults[key.serObject], prop)
    }

    override fun getChild(key: ClassBuilder<*>): ClassBuilder<*>? {
        return serObject[cbToString(key)]
    }

    override fun toView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.borderpane {

            top = borderpane {
                center = vbox {
                    addClass(Styles.parent)
                    label("Required? ${isRequired()}")
                    label("Type: ${type.rawClass}")
                    val cbParent = this@ComplexClassBuilder.parent
                    if (cbParent != null && cbParent is ComplexClassBuilder) {
                        val desc = cbParent.propInfo[key?.getPreviewValue()]?.description
                        if (!desc.isNullOrBlank()) {
                            label("Description: $desc")
                        }
                    }
                }
            }

            if (this@ComplexClassBuilder.serObject.isEmpty()) {
                center = borderpane {
                    top = separator()
                    center = vbox {
                        alignment = Pos.CENTER
                        textflow {
                            textAlignment = TextAlignment.CENTER

                            text("Class ")
                            text(type.rawClass.canonicalName) { font = Styles.monospaceFont }
                            text(" have no serializable properties")
                        }
                    }
                }

            } else {
                center = squeezebox(false) {
                    for ((name, child) in this@ComplexClassBuilder.serObject) {

                        fun getFoldTitle(cb: ClassBuilder<*>? = child): String {
                            //Star mean required, that's universal right? Otherwise we need to communicate this to the user
                            return "$name: ${cb?.getPreviewValue() ?: "(null)"}${if (isRequired()) " *" else ""} - ${propInfo[name]!!.type.rawClass.canonicalName}"
                        }

                        fold(getFoldTitle()) {

                            //Wait for the fold to be expanded for the first time to create the view, cb etc
                            expandedProperty().onChangeOnce {
                                val cb: ClassBuilder<*>
                                if (child == null) {
                                    //This should never be null as we are using the name of a property
                                    // well, if it is something has gone wrong, but not here!
                                    val createdCB = createClassBuilderFor(name.toCb())!!

                                    //update fold title before editing
                                    this.text = getFoldTitle(createdCB)
                                    cb = createdCB
                                } else {
                                    cb = child
                                }

                                cb.toView(this, controller)

                                //reflect changes in the title of the fold
                                cb.serObjectObservable.onChange {
                                    //text means title in this context
                                    this.text = getFoldTitle(cb)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getChildType(cb: ClassBuilder<*>): JavaType? {
        return propInfo[cbToString(cb)]?.type
    }

    override fun getPreviewValue(): String {
        return "Complex class of type ${type.rawClass.typeName}. ${if (parent == null) "Root builder" else "Child ${key?.getPreviewValue()} of ${parent.getPreviewValue()}"}"
    }

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> =
        this.serObject.mapKeys { it.key.toCb() }

    override fun isLeaf(): Boolean = false

    override fun isImmutable(): Boolean = false

    override fun toString(): String {
        return "Complex CB; type=$type)"
    }

    @Suppress("DuplicatedCode")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MapClassBuilder<*, *>) return false

        if (type != other.type) return false
        if (parent != other.parent) return false
        if (key != other.key) return false
        if (property != other.property) return false
        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (parent?.hashCode() ?: 0)
        result = 31 * result + key.hashCode()
        result = 31 * result + (property?.hashCode() ?: 0)
        return result
    }
}
