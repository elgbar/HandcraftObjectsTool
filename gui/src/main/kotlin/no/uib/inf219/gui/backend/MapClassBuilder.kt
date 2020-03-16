package no.uib.inf219.gui.backend


import com.fasterxml.jackson.databind.JavaType
import javafx.event.EventTarget
import javafx.scene.Node
import no.uib.inf219.extra.bindCbText
import no.uib.inf219.extra.toCb
import no.uib.inf219.gui.Styles
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.PropertyEditor
import tornadofx.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

/**
 * @author Elg
 */
//@JsonSerialize(using = MapCBSerializer::class)
class MapClassBuilder<K, out V>(
    override val type: JavaType,
    override val key: ClassBuilder<*>? = null,
    override val parent: ClassBuilder<*>?,
    override val property: ClassInformation.PropertyMetadata?
) : ClassBuilder<Map<K?, V?>> {

    override val serObject = HashMap<ClassBuilder<*>, ClassBuilder<*>?>()
    override val serObjectObservable = serObject.asObservable()

    override fun toView(parent: EventTarget, controller: ObjectEditorController): Node {

        return parent.splitpane {
            setDividerPositions(0.25)
            val con = ObjectEditorController(type, this@MapClassBuilder, controller)
            this += vbox {
                button("Add element") {
                    action {
                        val key = getClassBuilder(type.keyType, "key #${serObject.size}".toCb()) ?: return@action
                        val value =
                            getClassBuilder(type.contentType, "value #${serObject.size}".toCb()) ?: return@action
                        serObject[key] = value
                        controller.reloadView()
                    }
                }
                for ((key, value) in serObject) {
                    hbox {
                        style { addClass(Styles.parent) }

                        fun name(cb: ClassBuilder<*>?): String {
                            return cb?.key?.getPreviewValue() ?: "null"
                        }

                        button(name(key)) {

                            this.textProperty().bindCbText(key, ::name)

                            action {
                                con.select(key)
                            }
                        }
                        button(name(value)) {
                            if (value != null) {
                                this.textProperty().bindCbText(value, ::name)
                                action {
                                    con.select(value)
                                }
                            } else {
                                //TODO is this the correct thing to do?
                                isDisable = true
                            }
                        }
                    }
                }
            }
            this += find<PropertyEditor>(params = *arrayOf("controller" to con)).root
        }
    }

    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? {
        require(init == null || init.type == getChildType(key)) {
            "Given initial value have different type than expected. expected ${getChildType(key)} got ${init?.type}"
        }
        return serObject.computeIfAbsent(key) { init }
    }

    override fun getChild(key: ClassBuilder<*>): ClassBuilder<*>? {
        return serObject[key]
    }

    override fun resetChild(
        key: ClassBuilder<*>,
        element: ClassBuilder<*>?,
        restoreDefault: Boolean
    ) {
        //The map must have the given key
        require(serObject.containsKey(key)) { "Given key does not exist in this map class builder" }
        //But does the given element is allowed to be null,
        require(element == null || serObject[key] == element) { "Given value does not match with this map class builder's value of given key" }

        serObject.remove(key)
    }


    override fun getPreviewValue(): String {
        return serObject.map { it.key.getPreviewValue() + " -> " + it.value?.getPreviewValue() }
            .joinToString(", ")
    }

    override fun getChildType(cb: ClassBuilder<*>): JavaType? {
        //TODO what if we want to reference a key?
        return type.contentType
    }

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> = serObject

    override fun isLeaf(): Boolean = false

    override fun isImmutable(): Boolean = false

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

    override fun toString(): String {
        return "Map CB; key type=${type.keyType}, contained type=${type.contentType})"
    }
}
