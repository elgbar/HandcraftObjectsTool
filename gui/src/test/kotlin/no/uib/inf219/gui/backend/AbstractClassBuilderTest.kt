package no.uib.inf219.gui.backend

import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.test.GenericExample
import no.uib.inf219.test.GenericExampleWithAbstractDefault
import no.uib.inf219.test.precondition.AlwaysFalsePrecondition
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import no.uib.inf219.test.precondition.Precondition
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
class AbstractClassBuilderTest {
    @Test
    internal fun allowLoadingOfAbstractTypes_WhenSerializingItDirectly() {
        val real = AlwaysTruePrecondition()
        val cb = ObjectEditorController(Precondition::class.type(), real).root
        val cbObj = cb.toObject()
        assertEquals(real, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenField() {
        val real = GenericExample(AlwaysTruePrecondition())
        val cb = ObjectEditorController(real::class.type(), real).root
        val cbObj = cb.toObject()
        assertEquals(real, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenInArray() {
        val real = arrayOf(AlwaysTruePrecondition(), AlwaysFalsePrecondition())
        val type = mapper.typeFactory.constructArrayType(Precondition::class.java)
        val cb = ObjectEditorController(type, real).root
        val cbObj = cb.toObject() as Array<*>
        assertArrayEquals(real, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenDefaultInField() {
        val expected = GenericExampleWithAbstractDefault(AlwaysFalsePrecondition())
        val cb = ObjectEditorController(GenericExampleWithAbstractDefault::class.type()).root
        val cbObj = cb.toObject()
        assertEquals(expected, cbObj)
    }

    @Test
    internal fun allowLoadingOfAbstractTypes_WhenInMap_value() {
        val real = mapOf(true to AlwaysTruePrecondition())
        val type = mapper.typeFactory.constructMapType(
            Map::class.java,
            Boolean::class.java,
            Precondition::class.java
        )
        val cb = mapper.readValue<Any>(
            mapper.writeValueAsString(
                ObjectEditorController(
                    type,
                    real
                ).root.toObject()
            ), type
        )
        assertEquals(real, cb)
    }
}
