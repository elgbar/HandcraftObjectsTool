package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonTypeInfo
import no.uib.inf219.extra.type
import no.uib.inf219.gui.view.ControlPanelView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class TypeIdSupported {

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
    object UseClassAsPropertyProperty {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY)
    object UseMinimalClassAsPropertyProperty {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
    object UseNameAsPropertyProperty {}

    @Test
    internal fun useClassAsPropertyPropertyTest() {
        val expected = ControlPanelView.mapper.writeValueAsString(UseClassAsPropertyProperty)
        println("expected = $expected")

        val obj =
            ComplexClassBuilder<UseClassAsPropertyProperty>(UseClassAsPropertyProperty::class.type()).toObject()
        val json = ControlPanelView.mapper.writeValueAsString(obj)
        println("got = $json")
        assertEquals(UseClassAsPropertyProperty, obj)
        assertEquals(expected, json)
    }

    @Test
    internal fun useMinimalClassAsPropertyPropertyTest() {
        val expected = ControlPanelView.mapper.writeValueAsString(UseMinimalClassAsPropertyProperty)
        println("expected = $expected")

        val obj =
            ComplexClassBuilder<UseMinimalClassAsPropertyProperty>(UseMinimalClassAsPropertyProperty::class.type()).toObject()
        val json = ControlPanelView.mapper.writeValueAsString(obj)
        println("got = $json")
        assertEquals(UseMinimalClassAsPropertyProperty, obj)
        assertEquals(expected, json)
    }

    @Test
    internal fun useNameAsPropertyPropertyTest() {
        val expected = ControlPanelView.mapper.writeValueAsString(UseNameAsPropertyProperty)
        println("expected = $expected")

        val obj =
            ComplexClassBuilder<UseNameAsPropertyProperty>(UseNameAsPropertyProperty::class.type()).toObject()
        val json = ControlPanelView.mapper.writeValueAsString(obj)
        println("got = $json")
        assertEquals(UseNameAsPropertyProperty, obj)
        assertEquals(expected, json)
    }
}
