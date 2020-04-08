package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TreeItem
import javafx.scene.text.TextAlignment
import no.uib.inf219.extra.findChild
import no.uib.inf219.extra.onChange
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.serializers.ComplexClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode
import no.uib.inf219.gui.controllers.classBuilderNode.EmptyClassBuilderNode
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
class ComplexClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata? = null,
    override val item: TreeItem<ClassBuilderNode>
) : ParentClassBuilder() {

    /**
     * Hold information about the given property
     */
    internal val propInfo: Map<String, ClassInformation.PropertyMetadata>

    /**
     * Holds the default value for the given property
     */
    internal val propDefaults: MutableMap<String, Any?> = HashMap()

    /**
     * Information about the generics of [type], is `null` when the class does not have a generic type
     */
    val typeSerializer: TypeSerializer?

    val isJsonValueDelegator: Boolean

    override val serObject = HashMap<String, ClassBuilder?>().asObservable()
    override val serObjectObservable = serObject

    init {
        val (typeSer, propInfo, valueDelegator) = ClassInformation.serializableProperties(type)
        isJsonValueDelegator = valueDelegator
        typeSerializer = typeSer
        this.propInfo = propInfo

        //initiate all valid values to null or default
        // to allow for iteration when populating Node explorer
        for ((key, v) in this.propInfo) {
            propDefaults[key] = v.getDefaultInstance()
            if (propDefaults[key] != null || v.type.isPrimitive) {
                //only create a class builder for properties that has a default value
                // or is primitive (which always have default values)
                createChildClassBuilder(key.toCb(), item = TreeItem())
            } else {
                this.serObject[key] = null
            }
        }
    }

    private fun cbToString(cb: ClassBuilder?): String {
        return cb?.serObject as? String
            ?: kotlin.error("Wrong type of key was given. Expected a StringClassBuilder but got $cb")
    }

    override fun createChildClassBuilder(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder {
        val propName = cbToString(key)

        val prop = propInfo[propName]
        require(prop != null) { "The class $type does not have a property with the name '$propName'. Expected one of the following: ${propInfo.keys}" }
        require(init == null || init.type.isTypeOrSubTypeOf(getChildType(key)?.rawClass)) {
            "Given initial value have different type than expected. Expected a subclass of ${getChildType(key)} got ${init?.type}"
        }
        return serObject.computeIfAbsent(propName) {
            createChild(key, init, prop, item)
        } ?: kotlin.error("Failed to create class builder")
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ) {
        val propName = cbToString(key)

        require(serObject.containsKey(propName)) {
            "The class $type does not have a property with the name '$propName'. Expected one of the following: ${propInfo.keys}"
        }
        require(element == null || element === serObject[propName]) {
            "Given element to reset does not match with the internal element. element: $element, internal ${serObject[propName]}"
        }

        val item = item.findChild(key)

        val newProp = if (restoreDefault && propDefaults[propName] != null) {
            val prop: ClassInformation.PropertyMetadata = propInfo[propName] ?: kotlin.error("Given prop name is wrong")
            //must be set to null to trigger the change event!
            // stupid javafx
            serObject[propName] = null //use non observable map to to trigger on change event
            createChild(key, null, prop, item)
        } else {
            item.value = EmptyClassBuilderNode(
                key,
                this,
                item = item
            )
            null
        }
        serObject[propName] = newProp
    }

    private fun createChild(
        key: ClassBuilder,
        init: ClassBuilder?,
        prop: ClassInformation.PropertyMetadata,
        item: TreeItem<ClassBuilderNode> = TreeItem()
    ): ClassBuilder? {
        return if (init != null) {
            require(init.item == item) { "Given item does not match init's item, expected $item init's item ${init.item}" }
            require(init.property == prop) { "Given property does not match init's property, expected '$prop' init's property '${init.property}'" }
            require(init.key == key) { "Given key does not match init's key, expected $key init's key ${init.key}" }
            require(init.parent == this) { "Given initial class builder does not have this ($this) as it's parent, but rather ${init.parent}" }
            init
        } else getClassBuilder(prop.type, key, propDefaults[key.serObject], prop, item)
    }

    override fun getChild(key: ClassBuilder): ClassBuilder? {
        return serObject[cbToString(key)]
    }

    override fun createEditView(
        parent: EventTarget,
        controller: ObjectEditorController
    ): Node {
        return parent.borderpane {

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

                        fun getFoldTitle(cb: ClassBuilder?): String {
                            //Star mean required, that's universal right? Otherwise we need to communicate this to the user
                            return "$name: ${cb?.getPreviewValue() ?: "(null)"}${if (cb?.property?.required == true) " (*) " else ""} - ${propInfo[name]!!.type.rawClass.canonicalName}"
                        }

                        fold(getFoldTitle(child)) {

                            //Wait for the fold to be expanded for the first time to create the view, cb etc
                            expandedProperty().onChangeOnce {
                                val cb: ClassBuilder
                                if (child == null) {
                                    //This should never be null as we are using the name of a property
                                    // well, if it is something has gone wrong, but not here!
                                    cb = createChildClassBuilder(name.toCb())

                                    //update fold title before editing
                                    this.text = getFoldTitle(cb)
                                } else {
                                    cb = child
                                }

                                cb.createEditView(this, controller)

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

    override fun getChildType(key: ClassBuilder): JavaType? {
        return propInfo[cbToString(key)]?.type
    }

    override fun getChildPropertyMetadata(key: ClassBuilder): ClassInformation.PropertyMetadata? {
        return propInfo[cbToString(key)]
    }

    override fun getPreviewValue(): String {
        return "Complex class of type ${type.rawClass.typeName}. Child $key of ${parent.getPreviewValue()}"
    }

    override fun getSubClassBuilders(): Map<ClassBuilder, ClassBuilder?> =
        this.serObject.mapKeys { it.key.toCb() }

    override fun isImmutable(): Boolean = false

    override fun toString(): String {
        return "Complex CB; type=$type)"
    }
}
