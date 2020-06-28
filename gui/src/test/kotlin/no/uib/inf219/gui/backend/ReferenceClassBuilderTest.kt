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

package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.SimpleClassBuilder
import no.uib.inf219.gui.backend.cb.node
import no.uib.inf219.gui.backend.cb.parents.CollectionClassBuilder
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.reference.ReferenceClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.backend.events.ClassBuilderResetEvent
import no.uib.inf219.gui.backend.events.resetEvent
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.test.conv.Conversation
import no.uib.inf219.test.conv.Response
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ReferenceClassBuilderTest {

    @BeforeEach
    internal fun setUp() {
        resetEvent.clear()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    internal fun resolveReference() {

        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder
        cb.serObject[Conversation::name.name] = "Root conv name".toCb(Conversation::name.name.toCb(), cb)
        cb.serObject[Conversation::text.name] = "Root conv response".toCb(Conversation::text.name.toCb(), cb)

        val responses = CollectionClassBuilder(
            List::class.type() as CollectionLikeType,
            Conversation::responses.name.toCb(),
            parent = cb,
            item = TreeItem()
        )
        cb.serObject[no.uib.inf219.test.conv.Conversation::responses.name] = responses

        //create two responses
        val resp1 = ComplexClassBuilder(
            Response::class.type(),
            "#1".toCb(),
            responses,
            item = TreeItem()
        )
        resp1.serObject[Response::name.name] = "resp 1 name".toCb(Response::name.name.toCb(), resp1)
        resp1.serObject[Response::response.name] = "resp 1 response".toCb(Response::response.name.toCb(), resp1)

        val resp2 = ComplexClassBuilder(
            Response::class.type(),
            "#2".toCb(),
            responses,
            item = TreeItem()
        )
        resp2.serObject[Response::name.name] = "resp 2 name".toCb(Response::name.name.toCb(), resp2)
        resp2.serObject[Response::response.name] = "resp 2 response".toCb(Response::response.name.toCb(), resp2)

        //add the created responses to the list of responses
        responses.serObject.add(resp1)
        responses.serObject.add(resp2)


        val resp1CB =
            ComplexClassBuilder(
                Conversation::class.type(),
                Response::conv.name.toCb(),
                responses,
                item = TreeItem()
            )
        resp1CB.serObject[Conversation::name.name] =
            "response conv name".toCb(Conversation::name.name.toCb(), resp1CB)
        resp1CB.serObject[Conversation::text.name] =
            "response conv response".toCb(Conversation::text.name.toCb(), resp1CB)

        //here the trouble begins
        //both responses will bring up the same conversation
        resp1.serObject[Response::conv.name] = resp1CB
        resp2.serObject[Response::conv.name] =
            ReferenceClassBuilder(
                refKey = Response::conv.name.toCb(),
                refParent = resp1,
                key = 1.toCb(),
                parent = responses, item = TreeItem()
            )

        //Each response lead to a common conversation, now lets try convert this to a real conversation
        var converted: Conversation? = null
        assertDoesNotThrow {
            println(ControlPanelView.mapper.writeValueAsString(cb.serObject))
            converted = cb.toObject() as Conversation?
        }
        val convertedResponses = converted?.responses ?: fail("Compiled object is null")

        assertEquals(convertedResponses[0].conv, convertedResponses[1].conv)
        assertSame(convertedResponses[0].conv, convertedResponses[1].conv) {
            "The converted conversation responses are equal, but not the same object"
        }
    }

    @Test
    internal fun refIsReset_toDefault() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        //name have default value
        val orgKey = Conversation::name.name
        val org = cb.serObject[orgKey]
        assertNotNull(org)
        val orgKeyCb = org!!.key

        assertTrue(cb === org.parent)
        assertTrue(orgKeyCb === org.key)

        //text property is a reference to the name property in this example
        val refKey = Conversation::text.name

        val ref = ReferenceClassBuilder(
            orgKeyCb,
            cb,
            refKey.toCb(),
            cb,
            item = TreeItem()
        )
        ref.item.value = FilledClassBuilderNode(ref.key, ref, ref.parent)

        cb.serObject[refKey] = ref

        //then we remove the original
        cb.resetChild(orgKey.toCb(), restoreDefault = true) //<-- we create a new default
        val newOrg = cb.serObject[orgKey]
        assertNotNull(newOrg)
        assertFalse(newOrg === org)

        //so the reference should also be null by now
        assertTrue(newOrg === ref.serObject) { "Reference object has not been updated" }
    }

    @Test
    internal fun refIsReset_toNull() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        //name have default value
        val orgKey = Conversation::name.name
        val org = cb.serObject[orgKey]
        assertNotNull(org)
        val orgKeyCb = org!!.key

        assertTrue(cb === org.parent)
        assertTrue(orgKeyCb === org.key)

        //text property is a reference to the name property in this example
        val refKey = Conversation::text.name

        val ref = ReferenceClassBuilder(
            orgKeyCb,
            cb,
            refKey.toCb(),
            cb,
            item = TreeItem()
        )
        ref.item.value = FilledClassBuilderNode(ref.key, ref, ref.parent)

        cb.serObject[refKey] = ref

        //then we remove the original

        resetEvent(ClassBuilderResetEvent(org.item.value, false))
        cb.resetChild(orgKey.toCb(), restoreDefault = false) //<-- we remove the original
        assertNull(cb.serObject[orgKey])

        //so the reference should also be null by now
        assertNull(cb.serObject[refKey]) { "Reference has not removed itself" }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    internal fun chainedRefRemoval() {
        val controller = ObjectEditorController(Array<String>::class.type())
        val parent = controller.root as CollectionClassBuilder

        //What we will be referencing
        val child0Cb = parent.createNewChild() as SimpleClassBuilder<String>
        child0Cb.serObject = "Hello!"

        //Create a second element that is a ref to the first
        val c1 = parent.createNewChild()!! // create this first to not get any IndexOutOfBoundsException
        val child1Cb = ReferenceClassBuilder(child0Cb.key, parent, c1.key, parent, c1.item)
        child1Cb.item.value = FilledClassBuilderNode(child1Cb.key, child1Cb, child1Cb.parent)
        parent[child1Cb.key] = child1Cb

        //Create a third element that is a ref to the second
        val c2 = parent.createNewChild()!! // create this first to not get any IndexOutOfBoundsException
        val child2Cb = ReferenceClassBuilder(child1Cb.key, parent, c2.key, parent, c2.item)
        child2Cb.item.value = FilledClassBuilderNode(child2Cb.key, child2Cb, child2Cb.parent)
        parent[child2Cb.key] = child2Cb

        //they are now in a chain of references
        assertSame(child0Cb, child1Cb.serObject)
        assertSame(child1Cb, child2Cb.serObject)

        assertSame(child0Cb.type, child1Cb.type)
        assertSame(child1Cb.type, child2Cb.type)

        child0Cb.node.resetClassBuilder(null, false)

        assertTrue(parent.serObject.isEmpty()) { "Parent should have no children, but it has ${parent.serObject}" }
    }


    @Test
    internal fun twoRefsToSame_Removal() {
        val controller = ObjectEditorController(Array<String>::class.type())
        val parent = controller.root as CollectionClassBuilder

        //What we will be referencing
        @Suppress("UNCHECKED_CAST")
        val child0Cb = parent.createNewChild() as SimpleClassBuilder<String>
        child0Cb.serObject = "Hello!"

        //Create a second element that is a ref to the first
        val c1 = parent.createNewChild()!! // create this first to not get any IndexOutOfBoundsException
        val child1Cb = ReferenceClassBuilder(child0Cb.key, parent, c1.key, parent, c1.item)
        child1Cb.item.value = FilledClassBuilderNode(child1Cb.key, child1Cb, child1Cb.parent)
        parent[child1Cb.key] = child1Cb

        //Create a third element that is a ref to the second
        val c2 = parent.createNewChild()!! // create this first to not get any IndexOutOfBoundsException
        val child2Cb = ReferenceClassBuilder(child0Cb.key, parent, c2.key, parent, c2.item)
        child2Cb.item.value = FilledClassBuilderNode(child2Cb.key, child2Cb, child2Cb.parent)
        parent[child2Cb.key] = child2Cb

        //they are now in a chain of references
        assertSame(child0Cb, child1Cb.serObject)
        assertSame(child0Cb, child2Cb.serObject)

        assertSame(child0Cb.type, child1Cb.type)
        assertSame(child0Cb.type, child2Cb.type)

        child0Cb.node.resetClassBuilder(null, false)
        assertTrue(parent.serObject.isEmpty()) { "Parent should have no children, but it has ${parent.serObject}" }
    }
}
