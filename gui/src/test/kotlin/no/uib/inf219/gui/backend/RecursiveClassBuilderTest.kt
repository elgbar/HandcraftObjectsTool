package no.uib.inf219.gui.backend

import no.uib.inf219.extra.type
import no.uib.inf219.gui.view.ControlPanelView
import no.uib.inf219.test.UselessRecursiveObject
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.Assertions.*
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
        val rec = UselessRecursiveObject()
        rec.with = rec

        var json: String? = null
        assertDoesNotThrow {
            json = ControlPanelView.mapper.writeValueAsString(rec)
        }
        assertEquals("{\"@id\":1,\"with\":1}", json)
    }

    @Test
    internal fun canDeserializeRecursiveClass() {
        val json = "{\"@id\":1,\"with\":1}"

        val made: UselessRecursiveObject
        try {
            made = ControlPanelView.mapper.readValue(json, UselessRecursiveObject::class.java)
        } catch (e: Throwable) {
            fail<Any>("Could not read from json", e)
            return
        }

        val rec = UselessRecursiveObject()
        rec.with = rec

        assertTrue(made === made.with)
    }

    @Test
    internal fun canCreateRecursiveClass() {
        val rec = UselessRecursiveObject()
        rec.with = rec

        val cb = ComplexClassBuilder<UselessRecursiveObject>(UselessRecursiveObject::class.type(), "rec")
        cb.serObject[UselessRecursiveObject::with.name] = cb

        var created: UselessRecursiveObject? = null
        assertDoesNotThrow {
            created = cb.toObject()
        }

        assertNotNull(created)
        assertTrue(created === created!!.with)
    }
}
