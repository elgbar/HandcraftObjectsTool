package no.uib.inf219.gui.backend

import no.uib.inf219.extra.type
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.test.UselessRecursiveObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class RecursiveClassBuilderTest {

    @Test
    internal fun canSerialiseRecursiveClass() {
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
        cb.serObject[UselessRecursiveObject::with.name] = ReferenceClassBuilder(cb, null)

        var created: UselessRecursiveObject? = null
        assertDoesNotThrow {
            created = cb.toObject()
        }

        assertEquals(rec, created)
    }
}
