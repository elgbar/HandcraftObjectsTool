package no.uib.inf219.gui.backend

import io.github.classgraph.ClassGraph
import no.uib.inf219.extra.type
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.test.PrimitiveDefaultValueShowcase
import no.uib.inf219.test.UselessRecursiveObject
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import no.uib.inf219.test.precondition.Precondition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
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

    @Test
    internal fun canSerializseRecursiveClass() {
        val rec = UselessRecursiveObject(null)
        rec.with = rec

        var json: String? = null
        assertDoesNotThrow {
            json = ControlPanelView.mapper.writeValueAsString(rec)
        }
        assertEquals("{\"@id\":1,\"with\":1}", json)
    }

    @Test
    internal fun canCreateRecursiveClass() {
        val rec = UselessRecursiveObject(null)
        rec.with = rec

        val cb = ComplexClassBuilder<UselessRecursiveObject>(UselessRecursiveObject::class.type(), "rec")
        cb.serializationObject[UselessRecursiveObject::with.name] = cb

        var created: UselessRecursiveObject? = null
        assertDoesNotThrow {
            created = cb.toObject()
        }

        assertEquals(rec, created)
    }

    @Test
    internal fun OnlySerObjShouldBeSerialized() {

        fun checkClass(clazz: Class<*>) {
            val props = ClassInformation.serializableProperties(clazz.type()).second
            assertEquals(1, props.size) {
                "${clazz.canonicalName} does not have exactly one serializable property: $props"
            }
            assertTrue(props.containsKey(ClassBuilder<*>::serializationObject.name)) {
                "${clazz.canonicalName} have one serializable property but is not '${ClassBuilder<*>::serializationObject.name}', but rather '${props.keys.first()}'"
            }
        }

        checkClass(ClassBuilder::class.java)

        ClassGraph().enableClassInfo().scan().use { scanResult ->
            for (impl in scanResult.getClassesImplementing(ClassBuilder::class.qualifiedName).loadClasses(ClassBuilder::class.java)) {
                checkClass(impl)
            }
        }
    }
}
