package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.test.PrimitiveDefaultValueShowcase
import no.uib.inf219.test.conv.Conversation
import no.uib.inf219.test.conv.Response
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import no.uib.inf219.test.precondition.Precondition
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 *
 *
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ComplexClassBuilderTest {

    @Test
    internal fun canCreateAbstractTypes() {
        val created = ComplexClassBuilder<Precondition>(
            AlwaysTruePrecondition::class.type(), "preq"
        ).toObject()
        assertEquals(AlwaysTruePrecondition(), created)
    }

    @Test
    internal fun canCreateClassWithPrimitivesAndString() {
        val created = ComplexClassBuilder<PrimitiveDefaultValueShowcase>(
            PrimitiveDefaultValueShowcase::class.type(),
            "name"
        ).toObject()
        assertEquals(PrimitiveDefaultValueShowcase(42, 46, 0.1, 0.1f, true, 6, 1, 'a', "abc"), created)
    }


    @Suppress("UNCHECKED_CAST")
    @Test
    internal fun resolveReference() {

        val cb = ComplexClassBuilder<Conversation>(Conversation::class.type(), "conv")
        cb.serObject[Conversation::name.name] = "Root conv name".toCb(Conversation::name.name, cb)
        cb.serObject[Conversation::text.name] = "Root conv response".toCb(Conversation::text.name, cb)

        val responses = CollectionClassBuilder<List<Response>>(
            List::class.type() as CollectionLikeType,
            Conversation::responses.name,
            parent = cb
        )
        cb.serObject[no.uib.inf219.test.conv.Conversation::responses.name] = responses

        //create two responses
        val resp1 = ComplexClassBuilder<Response>(Response::class.type(), "#1", responses)
        resp1.serObject[Response::name.name] = "resp 1 name".toCb(Response::name.name, resp1)
        resp1.serObject[Response::response.name] = "resp 1 response".toCb(Response::response.name, resp1)

        val resp2 = ComplexClassBuilder<Response>(Response::class.type(), "#2", responses)
        resp2.serObject[Response::name.name] = "resp 2 name".toCb(Response::name.name, resp2)
        resp2.serObject[Response::response.name] = "resp 2 response".toCb(Response::response.name, resp2)

        //add the created responses to the list of responses
        responses.serObject.add(resp1)
        responses.serObject.add(resp2)


        val resp1CB = ComplexClassBuilder<Conversation>(Conversation::class.type(), Response::conv.name)
        resp1CB.serObject[Conversation::name.name] =
            "response conv name".toCb(Conversation::name.name, resp1CB)
        resp1CB.serObject[Conversation::text.name] =
            "response conv response".toCb(Conversation::text.name, resp1CB)

        //here the trouble begins
        //both responses will bring up the same conversation
        resp1.serObject[Response::conv.name] = resp1CB
        resp2.serObject[Response::conv.name] = ReferenceClassBuilder(resp1CB, resp2)

        //Each response lead to a common conversation, now lets try convert this to a real conversation
        val converted: Conversation = cb.toObject() ?: Assertions.fail("Compiled object is null")
        val convertedResponses = converted.responses

        //Each response lead to a common conversation
        assertEquals(convertedResponses[0].conv, convertedResponses[1].conv)
        //TODO allow for references, and not just clones
        Assertions.assertTrue(convertedResponses[0].conv === convertedResponses[1].conv) { "The converted conversation responses are equal, but not the same object" }
    }
}
