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
import no.uib.inf219.extra.findChild
import no.uib.inf219.gui.backend.cb.serializers.ClassBuilderSerializer
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation.PropertyMetadata

/**
 * An interface that is the super class of all object builder, the aim of this interface is to manage how to build a given type.
 * This is a a way to create classes by holding all included attributes as keys and their values as value in an internal map.
 *
 * It would be logical to think that this interface should have a generic type. But as we do not know _what_ that type will be at
 * runtime it would almost always just be [Any] when created. When do do know the type, we do so at compile time due to subclassing,
 * so each subclass can choose to have generic types.
 *
 * A more accurate name for this interface would be `type builder` as we as building other types than classes as well,
 * but due this was realized too late in development to make a change.
 *
 * @author Elg
 *
 * @see ParentClassBuilder Holds children with more class builders
 * @see SimpleClassBuilder Have generic types
 * @see no.uib.inf219.gui.backend.cb.reference.ReferenceClassBuilder Allows for references
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

    /**
     * The expected [JavaType] this class builder will be serialized to
     */
    @get:JsonIgnore
    val type: JavaType

    /**
     * The parent of this class builder in the property graph.
     * If [parent] the same as `this`, this is the root builder
     *
     * @see no.uib.inf219.gui.backend.cb.isDescendantOf
     * @see ObjectEditorController.RootDelegator
     */
    @get:JsonIgnore
    val parent: ParentClassBuilder

    /**
     * Key of the property to access this from this [parent]
     */
    @get:JsonIgnore
    val key: ClassBuilder

    /**
     * The property this class builder is creating, used for gaining additional metadata about what we're creating
     */
    @get:JsonIgnore
    val property: PropertyMetadata?

    /**
     * The item representing this class builder graphically
     */
    @get:JsonIgnore
    val item: TreeItem<ClassBuilderNode>
        get() = parent.item.findChild(key)

    /**
     * If this is a leaf in the class builder property graph
     *
     * @see no.uib.inf219.gui.backend.cb.reference.ReferenceClassBuilder
     * @see no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
     * @see no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
     */
    @JsonIgnore
    fun isLeaf(): Boolean

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


    ///////////////////////
    // Visual components //
    ///////////////////////

    /**
     * Visual representation (and possibly modification) of this class builder
     */
    fun createEditView(parent: EventTarget, controller: ObjectEditorController): Node

    /**
     * Event called when a [KeyEvent] is triggered while [item] is selected in [no.uib.inf219.gui.view.NodeExplorerView]
     */
    fun onNodeKeyEvent(event: KeyEvent, controller: ObjectEditorController) {
        if (event.code == KeyCode.DELETE) {
            controller.deleteSelected(!event.isShiftDown)
        }
    }

    /**
     * Allow class builder to customize what is displayed when right clicking [item] in [no.uib.inf219.gui.view.NodeExplorerView]
     *
     * @return if a separator should be added before the items added here
     */
    fun createContextMenu(menu: ContextMenu, controller: ObjectEditorController): Boolean = false
}
