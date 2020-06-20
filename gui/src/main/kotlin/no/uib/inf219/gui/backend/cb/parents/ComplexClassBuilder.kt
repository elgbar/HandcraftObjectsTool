package no.uib.inf219.gui.backend.cb.parents

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
import no.uib.inf219.extra.onChangeUntil
import no.uib.inf219.extra.type
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.backend.cb.createClassBuilder
import no.uib.inf219.gui.backend.cb.serializers.ComplexClassBuilderSerializer
import no.uib.inf219.gui.backend.cb.simple.StringClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.EmptyClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView.mapper
import tornadofx.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * A class builder intended to be used for normal classes. It is "complex" due containing multiple other [ClassBuilder]s.
 *
 * @author Elg
 */
@JsonSerialize(using = ComplexClassBuilderSerializer::class)
class ComplexClassBuilder(
    override val type: JavaType,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val property: ClassInformation.PropertyMetadata? = null,
    override val item: TreeItem<ClassBuilderNode>,
    val init: Any? = null
) : ParentClassBuilder() {

    /**
     * Hold information about the given property
     */
    internal val propInfo: Map<String, ClassInformation.PropertyMetadata>

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

        //is it really safe to do it this way?
        //It's not really fast, but is there any way to get all wanted values with the properties?
        val initMap: Map<String, Any?>? =
            if (init != null) {
                val type = mapper.typeFactory.constructMapType(Map::class.java, String::class.java, Any::class.java)
                mapper.convertValue<Map<String, Any?>>(init, type)
            } else {
                null
            }

        //initiate all valid values to null or default
        // to allow for iteration when populating Node explorer
        for ((key, v) in this.propInfo) {

            val initValue = initMap?.get(key)
            if (initValue != null) {
                val keyCb = key.toCb()
                //The value must be converted back to the expected value!
                val realValue = mapper.convertValue<Any>(initValue, v.type)
                val init =
                    createClassBuilder(realValue.javaClass.type(), keyCb, this, realValue, propInfo[key], TreeItem())
                        ?: kotlin.error("Failed to load property $key of $this")


                checkChildValidity(keyCb, init)
                checkItemValidity(init, init.item)

                this.serObject[key] = init

            } else if (v.hasValidDefaultInstance()) {
                //only create a class builder for properties that has a default value
                // or is primitive (which always have default values)
                this.createChild(key.toCb(), item = TreeItem())
            } else {
                this.serObject[key] = null
            }
        }
    }

    private fun cbToString(cb: ClassBuilder?): String {
        return cb?.serObject as? String
            ?: kotlin.error("Wrong type of key was given. Expected a StringClassBuilder but got $cb")
    }

    override fun createChild(
        key: ClassBuilder,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder? {
        val propName = cbToString(key)
        return serObject.computeIfAbsent(propName) {
            createChild0(key as StringClassBuilder, init, item)
        }
    }

    override fun resetChild(
        key: ClassBuilder,
        element: ClassBuilder?,
        restoreDefault: Boolean
    ) {
        val propName = cbToString(key)
        val meta: ClassInformation.PropertyMetadata = propInfo[propName]
            ?: throw IllegalArgumentException("The class $type does not have a property with the name '$propName'. Expected one of the following: ${propInfo.keys}")

        require(element == null || element === serObject[propName]) {
            "Given element to reset does not match with the internal element. Given: $element | internal ${serObject[propName]}"
        }

        val item = item.findChild(key)

        val newProp = if (restoreDefault && meta.hasValidDefaultInstance()) {
            createChild0(key as StringClassBuilder, null, item)
        } else {
            item.value = EmptyClassBuilderNode(key, this, item)
            null
        }
        serObject[propName] = newProp
    }

    override fun set(key: ClassBuilder, child: ClassBuilder?) {
        if (child == null) {
            resetChild(key, restoreDefault = false)
            return
        }
        checkChildValidity(key, child)
        checkItemValidity(child)

        val propName = cbToString(key)
        serObject[propName] = child
    }

    private fun createChild0(
        key: SimpleClassBuilder<String>,
        init: ClassBuilder?,
        item: TreeItem<ClassBuilderNode>
    ): ClassBuilder? {

        val prop: ClassInformation.PropertyMetadata = propInfo[key.serObject]
            ?: kotlin.error("The class $type does not have a property with the name '${key.serObject}'. Expected one of the following: ${propInfo.keys}")

        return if (init != null) {
            checkChildValidity(key, init)
            checkItemValidity(init, item)
            init
        } else {
            val meta = propInfo[cbToString(key)] ?: kotlin.error("Failed to find meta for ${key.getPreviewValue()}")
            val defInstance = meta.getDefaultInstance()
            val childType = if (defInstance != null) {
                val tmpType = defInstance::class.type()
                //do not use instanced types for collections, arrays, or maps
                //as the handling of those types are special
                if (tmpType.isCollectionLikeType || tmpType.isMapLikeType) {
                    prop.type
                } else {
                    tmpType
                }
            } else prop.type

            createClassBuilder(childType, key, this, defInstance, prop, item)
        }
    }

    override fun get(key: ClassBuilder): ClassBuilder? {
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
                            expandedProperty().onChangeUntil({ this.isExpanded }) {
                                val cb: ClassBuilder
                                if (child == null) {
                                    //This should never be null as we are using the name of a property
                                    // well, if it is something has gone wrong, but not here!
                                    val newCb = this@ComplexClassBuilder.createChild(name.toCb())
                                    if (newCb == null) {
                                        this@fold.isExpanded = false
                                        return@onChangeUntil
                                    }
                                    cb = newCb

                                    //update fold title before editing
                                    this.text = getFoldTitle(newCb)
                                } else {
                                    cb = child
                                }

                                cb.createEditView(this, controller)

                                //reflect changes in the title of the fold
                                cb.serObjectObservable.onChange {
                                    //text means title in this context
                                    this@fold.text = getFoldTitle(cb)
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

    override fun getChildPropertyMetadata(key: ClassBuilder): ClassInformation.PropertyMetadata {
        return propInfo[cbToString(key)] ?: kotlin.error("Unknown child with key ${key.getPreviewValue()}")
    }

    override fun getPreviewValue(): String {
        return "Class of type ${type.rawClass.simpleName}"
    }

    override fun getChildren(): Map<ClassBuilder, ClassBuilder?> =
        this.serObject.mapKeys { it.key.toCb() }

    override fun isImmutable(): Boolean = false

    override fun toString(): String {
        return "Complex CB; type=$type)"
    }
}
