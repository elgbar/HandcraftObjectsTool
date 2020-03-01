package no.uib.inf219.gui.backend

import no.uib.inf219.extra.type
import no.uib.inf219.test.AlwaysTruePrerequisite
import no.uib.inf219.test.Prerequisite
import no.uib.inf219.test.PrimitiveDefaultValueShowcase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ComplexClassBuilderTest {

    @Test
    internal fun canCreateAbstractTypes() {
        val created = ComplexClassBuilder<Prerequisite>(AlwaysTruePrerequisite::class.type(), "preq").toObject()
        Assertions.assertEquals(AlwaysTruePrerequisite(), created)
    }

    @Test
    internal fun canCreateClassWithPrimitivesAndString() {
        val created = ComplexClassBuilder<PrimitiveDefaultValueShowcase>(
            PrimitiveDefaultValueShowcase::class.type(),
            "name"
        ).toObject()
        Assertions.assertEquals(PrimitiveDefaultValueShowcase(42, 46, 0.1, 0.1f, true, 6, 1, 'a', "abc"), created)
    }
}
