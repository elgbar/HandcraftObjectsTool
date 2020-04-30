package no.uib.inf219.gui.backend.cb.api

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import javafx.beans.Observable
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.backend.cb.serializers.ClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation.PropertyMetadata

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 *
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
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
    fun onNodeMouseEvent(event: MouseEvent, controller: ObjectEditorController) {}

    fun onNodeKeyEvent(event: KeyEvent, controller: ObjectEditorController) {
        if (event.code == KeyCode.DELETE || event.code == KeyCode.DELETE) {
            controller.deleteSelected(!event.isShiftDown)
        }
    }

    /**
     * Allow class builder to customize what is displayed when right clicking on it's node.
     *
     * @return if a separator should be added before the items added here
     */
    fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean = false
}
