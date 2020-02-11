package no.uib.inf219.gui.backend

import javafx.stage.Stage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class SimpleClassBuilderTest {

    lateinit var cb: SimpleClassBuilder<String>

    @Start
    fun onStart(stage: Stage) {
        cb = createCB()
    }

    private fun createCB(): SimpleClassBuilder<String> {
        return ClassBuilder.StringClassBuilder(INIT_VAL)
    }

    companion object {
        const val INIT_VAL = "This is a string"
    }

    @Test
    fun reset() {
        assertEquals(cb.value, INIT_VAL)
        cb.value = INIT_VAL + "test"
        assertNotEquals(cb.value, INIT_VAL)
        cb.reset()
        assertEquals(cb.value, INIT_VAL)
    }

    @Test
    fun isLeaf() {
        assertTrue(cb.isLeaf())
    }
}
