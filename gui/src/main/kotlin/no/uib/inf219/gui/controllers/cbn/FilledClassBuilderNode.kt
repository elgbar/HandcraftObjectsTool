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

import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder

/**
 * @author Elg
 */
data class FilledClassBuilderNode(
    override val key: ClassBuilder,
    override val cb: ClassBuilder,
    override val parent: ParentClassBuilder,
    override val item: TreeItem<ClassBuilderNode> = cb.item,
    override val allowReference: Boolean = true
) : ClassBuilderNode {

    override fun ensurePresentClassBuilder(tree: TreeView<ClassBuilderNode>): FilledClassBuilderNode {
        return this
    }

    override fun toString(): String {
        return "FilledClassBuilderNode(key=${key.getPreviewValue()}, cb=${cb.getPreviewValue()} parent=${parent.getPreviewValue()})"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FilledClassBuilderNode

        if (key != other.key) return false
        if (cb != other.cb) return false
        if (parent != other.parent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + cb.hashCode()
        result = 31 * result + parent.hashCode()
        return result
    }
}
