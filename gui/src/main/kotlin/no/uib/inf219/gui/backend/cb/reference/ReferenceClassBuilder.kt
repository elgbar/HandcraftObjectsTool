/*
 * Copyright 2020 Karl Henrik Elg Barlinn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.uib.inf219.gui.backend.cb.reference

import com.fasterxml.jackson.databind.JavaType
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.textCb
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.checkNoCycle
import no.uib.inf219.gui.backend.cb.isDescendantOf
import no.uib.inf219.gui.backend.cb.node
import no.uib.inf219.gui.backend.cb.path
import no.uib.inf219.gui.backend.events.ClassBuilderResetEvent
import no.uib.inf219.gui.backend.events.resetEvent
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.ClassBuilderNode
import no.uib.inf219.gui.loader.ClassInformation
import tornadofx.hbox
import tornadofx.onDoubleClick

/**
 * A reference to another class builder. This class builder will pretend to have the same [property] and [type] as what
 * it is referring to.
 *
 * When the referring object is set to `null` this will also be set to `null`. References to references are allowed.
 *
 * @author Elg
 */
class ReferenceClassBuilder(
    val refKey: ClassBuilder,
    val refParent: ParentClassBuilder,
    override val key: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val item: TreeItem<ClassBuilderNode>
) : ClassBuilder {

    override val serObject: ClassBuilder
        get() {
            val so = refParent[refKey]
                ?: error("Failed to find a serObject with the given reference parent and ref key. Cannot make a reference to a null class builder")
            if (so !== serObjectObservable.value) {
                serObjectObservable.value = so
                checkRefCycle()
            }
            return so
        }

    override val property: ClassInformation.PropertyMetadata? get() = parent.getChildPropertyMetadata(key)
    override val type: JavaType get() = serObject.type
    override val serObjectObservable = SimpleObjectProperty<ClassBuilder>()

    private val event: (ClassBuilderResetEvent) -> Unit

    private fun checkRefCycle() {
        require(this.checkNoCycle(key, parent)) {
            "Direct cycle detected, the object we're referencing is this!"
        }
    }

    init {

        event = { (cbn, _) ->
            if (cbn.parent === refParent && cbn.key.serObject == refKey.serObject || refParent.isDescendantOf(cbn)) {
                removeResetEvent()
                // make sure the removal always takes place on the same thread
                this.node.resetClassBuilder(null, true)
            } else if (cbn === node) {
                // the reference it self is being removed,
                // just remove the event without calling reset node (as that's whats happening now anyway)
                removeResetEvent()
            }
        }
        resetEvent += event
    }

    private fun removeResetEvent() {
        resetEvent -= event
    }

    override fun createEditView(parent: EventTarget, controller: ObjectEditorController): Node {
        return parent.hbox {
            alignment = Pos.CENTER

            onDoubleClick {
                controller.select(serObject)
            }

            textCb(serObject) {
                """
                    This class builder is only a reference to object at ${this.path}.
                    Double click to edit the referenced class builder.
                    
                    Preview: ${this.getPreviewValue()}
                """.trimIndent()
            }
        }
    }

    override fun getPreviewValue() =
        "Ref to '${refKey.getPreviewValue()}' of '${refParent.getPreviewValue()}'"

    override fun isLeaf(): Boolean = true
    override fun isImmutable() = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ReferenceClassBuilder) return false

        // ser objects must be same object
        if (parent !== other.parent) return false
        if (key != other.key) return false
        if (refParent !== other.refParent) return false
        if (refKey.serObject != other.refKey.serObject) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parent.hashCode()
        result = 31 * result + key.hashCode()
        return result
    }

    override fun toString(): String {
        return "Ref CB; ref to '${refKey.getPreviewValue()}' of '${refParent.getPreviewValue()}')"
    }
}
