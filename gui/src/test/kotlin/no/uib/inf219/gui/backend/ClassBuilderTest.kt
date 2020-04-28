package no.uib.inf219.gui.backend

import no.uib.inf219.extra.FAKE_ROOT
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class ClassBuilderTest {


    @Test
    internal fun getClassBuilder_failOnTypeMismatch() {
        assertThrows(IllegalArgumentException::class.java) {
            ClassBuilder.createClassBuilder(
                String::class.type(), key = "key".toCb(), parent = FAKE_ROOT, value = 2
            )
        }
    }

    @Test
    internal fun getClassBuilder_worksForPrimitives() {
        assertDoesNotThrow {
            ClassBuilder.createClassBuilder(
                Boolean::class.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                value = true
            )
        }

        assertDoesNotThrow {
            ClassBuilder.createClassBuilder(
                Boolean::class.javaPrimitiveType!!.type(),
                key = "key".toCb(),
                parent = FAKE_ROOT,
                value = true
            )
        }
    }

    @Disabled
    @Test
    internal fun resetChild_dontRestore_collection() {
        TODO("not implemented")
    }

    @Disabled
    @Test
    internal fun resetChild_dontRestore_map() {
        TODO("not implemented")
    }
}
