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

package no.uib.inf219.extra

import javafx.scene.control.TreeItem
import no.uib.inf219.gui.backend.CollectionClassBuilderTest.Companion.listStrType
import no.uib.inf219.gui.backend.cb.FAKE_ROOT
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.api.ParentClassBuilder
import no.uib.inf219.gui.backend.cb.isDescendantOf
import no.uib.inf219.gui.backend.cb.parents.CollectionClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderUtilKtTest {

    @Test
    internal fun isDescendantOf_legitChild() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        val child = parent.createChild(0.toCb(immutable = false), item = TreeItem())
        assertNotNull(child)

        assertTrue(child!!.isDescendantOf(parent))
    }

    @Test
    internal fun isDescendantOf_legitGrandChild() {

        val listListType =
            ControlPanelView.mapper.typeFactory.constructCollectionType(List::class.java, List::class.java)

        val parent = ObjectEditorController(listListType).root as CollectionClassBuilder

        val child = parent.createChild(
            0.toCb(immutable = false),
            item = TreeItem()
        ) as ParentClassBuilder

        val grandChild = child.createChild(
            0.toCb(immutable = false),
            item = TreeItem()
        ) as ClassBuilder

        assertTrue(child.isDescendantOf(parent))
        assertTrue(grandChild.isDescendantOf(child))
        assertTrue(grandChild.isDescendantOf(parent))
    }

    @Test
    internal fun isDescendantOf_illegitimateChild() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        val child = parent.createChild(
            0.toCb(immutable = false),
            item = TreeItem()
        )
        assertNotNull(child)
        parent.resetChild(0.toCb(), restoreDefault = false)

        assertFalse(child!!.isDescendantOf(parent))
    }

    @Test
    internal fun isDescendantOf_self() {
        val parent = ObjectEditorController(listStrType).root as CollectionClassBuilder

        assertTrue(parent.isDescendantOf(parent))
    }

    @Test
    internal fun isDescendantOf_rootSelf() {
        assertTrue(FAKE_ROOT.isDescendantOf(FAKE_ROOT))
    }
}
