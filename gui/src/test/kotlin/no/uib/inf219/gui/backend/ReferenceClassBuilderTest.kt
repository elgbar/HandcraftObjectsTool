package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.type.CollectionLikeType
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ReferenceClassBuilderTest {

    @Test
    internal fun referencePointToSameObject() {

        val created = CollectionClassBuilder<Any>(ArrayList::class.type() as CollectionLikeType, "list")
        val strCB = "test123 :)".toCb(parent = created, immutable = false)
        created.serObject.add(strCB)
        created.serObject.add(strCB)

        assertTrue(strCB.toObject() === strCB.toObject())

        assertEquals(listOf(strCB.toObject(), strCB.toObject()), created.toObject())

        strCB.serObject = "new text"


        assertEquals(listOf(strCB.toObject(), strCB.toObject()), created.toObject())
    }
}
