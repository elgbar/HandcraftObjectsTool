package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.test.conv.Conversation
import no.uib.inf219.test.conv.Response
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ReferenceClassBuilderTest {


    @Suppress("UNCHECKED_CAST")
    @Test
    internal fun resolveReference() {

        val cb = ComplexClassBuilder(
            Conversation::class.type(),
            key = "key".toCb(),
            parent = SimpleClassBuilder.FAKE_ROOT,
            item = TreeItem()
        )
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
        val resp1 = ComplexClassBuilder(Response::class.type(), "#1".toCb(), responses, item = TreeItem())
        resp1.serObject[Response::name.name] = "resp 1 name".toCb(Response::name.name.toCb(), resp1)
        resp1.serObject[Response::response.name] = "resp 1 response".toCb(Response::response.name.toCb(), resp1)

        val resp2 = ComplexClassBuilder(Response::class.type(), "#2".toCb(), responses, item = TreeItem())
        resp2.serObject[Response::name.name] = "resp 2 name".toCb(Response::name.name.toCb(), resp2)
        resp2.serObject[Response::response.name] = "resp 2 response".toCb(Response::response.name.toCb(), resp2)

        //add the created responses to the list of responses
        responses.serObject.add(resp1)
        responses.serObject.add(resp2)


        val resp1CB =
            ComplexClassBuilder(Conversation::class.type(), Response::conv.name.toCb(), responses, item = TreeItem())
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
        val cb = ComplexClassBuilder(
            Conversation::class.type(),
            key = "key".toCb(),
            parent = SimpleClassBuilder.FAKE_ROOT,
            item = TreeItem()
        )
        //name have default value
        val orgKey = Conversation::name.name
        val org = cb.serObject[orgKey] ?: fail("org is null")

        //text property is a reference to the name property in this example
        val refKey = Conversation::text.name
        val ref = ReferenceClassBuilder(
            Conversation::name.name.toCb(),
            cb,
            Conversation::text.name.toCb(),
            cb,
            item = TreeItem()
        )
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
        val cb = ClassBuilder.createClassBuilder(
            Conversation::class.type(),
            "key".toCb(),
            SimpleClassBuilder.FAKE_ROOT
        ) as ComplexClassBuilder?
            ?: fail("Failed to create class builder for Conversation")

        //name have default value
        val orgKey = Conversation::name.name
        assertNotNull(cb.serObject[orgKey])

        //text property is a reference to the name property in this example
        val refKey = Conversation::text.name
        val ref = ReferenceClassBuilder(
            Conversation::name.name.toCb(),
            cb,
            Conversation::text.name.toCb(),
            cb,
            item = TreeItem()
        )
        cb.serObject[refKey] = ref

        //then we remove the original
        cb.resetChild(orgKey.toCb(), restoreDefault = false) //<-- we remove the original
        assertNull(cb.serObject[orgKey])

        //so the reference should also be null by now
        assertNull(cb.serObject[refKey]) { "Reference has not removed itself" }
    }
}
