package no.uib.inf219.gui.backend

import no.uib.inf219.extra.type
import no.uib.inf219.test.PrimitiveDefaultValueShowcase
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import no.uib.inf219.test.precondition.Precondition
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 *
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderTest {

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
}
