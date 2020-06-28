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

package no.uib.inf219.gui.controllers.cbn

import com.fasterxml.jackson.databind.JavaType
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.extra.reselect
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.events.ClassBuilderResetEvent
import no.uib.inf219.gui.backend.events.resetEvent
import no.uib.inf219.gui.loader.ClassInformation

/**
 * Represents a node in the tree of [no.uib.inf219.gui.view.NodeExplorerView]
 *
 * @author Elg
 */
interface ClassBuilderNode {

    /**
     * Key of this class builder, doubles up as the text displayed in the tree view
     */
    val key: ClassBuilder

    /**
     * The class builder this node references.
     */
    val cb: ClassBuilder?

    /**
     * The parent of this node
     */
    val parent: ParentClassBuilder

    val item: TreeItem<ClassBuilderNode>

    /**
     * It it is allowed to create a reference ([no.uib.inf219.gui.backend.ReferenceClassBuilder]) to this node.
     *
     * @see no.uib.inf219.gui.backend.MapClassBuilder
     */
    val allowReference: Boolean

    /**
     * Ensure this node has a class builder present. If null is returned the user somehow failed to to create a child class builder
     */
    fun ensurePresentClassBuilder(tree: TreeView<ClassBuilderNode>): FilledClassBuilderNode?

    val property: ClassInformation.PropertyMetadata? get() = parent.getChildPropertyMetadata(key)
    val type: JavaType? get() = parent.getChildType(key)

    /**
     * Reset the given class builder
     */
    fun resetClassBuilder(
        tree: TreeView<ClassBuilderNode>? = null,
        restoreDefault: Boolean
    ) {
        resetEvent(ClassBuilderResetEvent(this, restoreDefault))
        item.children.clear()
        parent.resetChild(key, cb, restoreDefault)
        tree?.reselect()
    }

    operator fun component1(): ClassBuilder {
        return key
    }

    operator fun component2(): ClassBuilder? {
        return cb
    }

    operator fun component3(): ParentClassBuilder {
        return parent
    }
}
