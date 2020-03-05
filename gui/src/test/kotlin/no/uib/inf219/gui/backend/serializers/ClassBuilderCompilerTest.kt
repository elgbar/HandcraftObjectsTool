package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.databind.type.CollectionLikeType
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.CollectionClassBuilder
import no.uib.inf219.gui.backend.ComplexClassBuilder
import no.uib.inf219.gui.backend.ReferenceClassBuilder
import no.uib.inf219.test.UselessRecursiveObject
import no.uib.inf219.test.conv.Conversation
import no.uib.inf219.test.conv.Response
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderCompilerTest {

    @Test
    internal fun resolveSimpleReference() {

        val cb = ComplexClassBuilder<UselessRecursiveObject>(UselessRecursiveObject::class.type(), "rec")
        cb.serObject[UselessRecursiveObject::with.name] = ReferenceClassBuilder(cb, cb)


        val compiled = ClassBuilderCompiler.build(cb)

        //cannot check equality with map as it does not expect a cyclic reference
        assertTrue(compiled is Map<*, *>)

        assertDoesNotThrow {
            @Suppress("UNCHECKED_CAST")
            val compiledMap = compiled as Map<String, Any?>
            assertEquals(compiled, compiledMap[UselessRecursiveObject::with.name])
        }
    }

    @Test
    internal fun resolveListReference() {

        val cb = CollectionClassBuilder<List<AlwaysTruePrecondition>>(List::class.type() as CollectionLikeType, "list")
        val obj = ComplexClassBuilder<AlwaysTruePrecondition>(AlwaysTruePrecondition::class.type(), "atp", cb)
        cb.serObject.add(obj)
        cb.serObject.add(ReferenceClassBuilder(obj, cb))

        //{"responses":[{,"conv":{"name":"a","responses":[],"text":"b"}},{"response":"","name":"","conv":"2c142310-bfee-4021-90b4-e8fb871a0c43"}],"text":""}

        val compiled = ClassBuilderCompiler.build(cb) as List<*>


        assertEquals(2, compiled.size)
        assertTrue(compiled[0] === compiled[1])

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

        val compiled: Map<String, Any> = ClassBuilderCompiler.build(cb) as Map<String, Any>

        //Each response lead to a common conversation

        val compResponses: List<Any> = compiled[Conversation::responses.name] as List<Any>
        val response1: Map<String, Any> = compResponses[0] as Map<String, Any>
        val response2: Map<String, Any> = compResponses[0] as Map<String, Any>

        assertEquals(response1[Response::conv.name], response2[Response::conv.name])
        assertTrue(response1[Response::conv.name] === response2[Response::conv.name]) { "The compiled conversation responses are not the same object" }

        //Each response lead to a common conversation, now lets try convert this to a real conversation
        val converted: Conversation = cb.toObject() ?: fail("Compiled object is null")
        val compiledResponses = converted.responses

        //Each response lead to a common conversation
        assertEquals(compiledResponses[0].conv, compiledResponses[1].conv)
        //TODO allow for references, and not just clones
//        assertTrue(compiledResponses[0].conv === compiledResponses[1].conv) { "The converted conversation responses are equal, but not the same object" }
    }
}
