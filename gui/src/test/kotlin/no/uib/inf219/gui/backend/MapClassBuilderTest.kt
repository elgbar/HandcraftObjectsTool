package no.uib.inf219.gui.backend

import javafx.scene.control.TreeItem
import no.uib.inf219.extra.findChild
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.parents.MapClassBuilder
import no.uib.inf219.gui.backend.cb.parents.MapClassBuilder.Companion.ENTRY_KEY
import no.uib.inf219.gui.backend.cb.parents.MapClassBuilder.Companion.ENTRY_VALUE
import no.uib.inf219.gui.backend.cb.parents.MapClassBuilder.Companion.keyCb
import no.uib.inf219.gui.backend.cb.parents.MapClassBuilder.Companion.valueCb
import no.uib.inf219.gui.backend.cb.reference.ReferenceClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.gui.controllers.cbn.FilledClassBuilderNode
import no.uib.inf219.gui.view.ControlPanelView.mapper
import no.uib.inf219.test.UselessRecursiveObject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class MapClassBuilderTest {

    companion object {
        val mapStrStrType
            get() = mapper.typeFactory.constructMapType(Map::class.java, String::class.java, String::class.java)
    }

    @Test
    internal fun canSerializeEmptyMap() {
        val cb = ObjectEditorController(mapStrStrType).root
        assertEquals(emptyMap<String, String>(), cb.toObject())
    }

    @Test
    internal fun canSerializeMapOfSizeOne() {
        val parent = ObjectEditorController(mapStrStrType).root as MapClassBuilder
        assertNotNull(parent)

        val entry0 =
            parent.createChild("0".toCb(), item = TreeItem()) as ComplexClassBuilder

        val keyVal = "test key"
        val valueVal = "test value"

        val key =
            keyVal.toCb(keyCb, entry0, entry0.propInfo[ENTRY_KEY], item = entry0.item.findChild(keyCb))
        key.item.value = FilledClassBuilderNode(keyCb, key, entry0)
        entry0[keyCb] = key

        val value =
            valueVal.toCb(valueCb, entry0, entry0.propInfo[ENTRY_VALUE], item = entry0.item.findChild(valueCb))
        value.item.value = FilledClassBuilderNode(valueCb, value, entry0)

        entry0[valueCb] = value

        assertEquals(keyVal, key.toObject())
        assertEquals(valueVal, value.toObject())


        var map: Any? = null
        assertDoesNotThrow {
            println("mapper.writeValueAsString(parent) = ${mapper.writeValueAsString(parent)}")
            map = parent.toObject()
        }

        assertEquals(mapOf(keyVal to valueVal), map)
    }

    @Test
    internal fun canSerializeMapOfSizeOne_Ref() {
        val parent = ObjectEditorController(mapStrStrType).root as MapClassBuilder
        assertNotNull(parent)

        val entry0 =
            parent.createChild("0".toCb(), item = TreeItem()) as ComplexClassBuilder

        val keyVal = "test key"

        val key =
            keyVal.toCb(keyCb, entry0, entry0.propInfo[ENTRY_KEY], item = entry0.item.findChild(keyCb))
        key.item.value = FilledClassBuilderNode(keyCb, key, entry0)
        entry0[keyCb] = key

        val value = ReferenceClassBuilder(
            refKey = keyCb,
            refParent = entry0,
            key = valueCb,
            parent = entry0,
            item = entry0.item.findChild(valueCb)
        )
        value.item.value = FilledClassBuilderNode(valueCb, value, entry0)

        entry0[valueCb] = value

        assertEquals(keyVal, key.toObject())
        assertEquals(keyVal, value.toObject())

        var map: Any? = null
        assertDoesNotThrow {
            println("mapper.writeValueAsString(parent) = ${mapper.writeValueAsString(parent)}")
            map = parent.toObject()
        }

        assertEquals(mapOf(keyVal to keyVal), map)
    }

    @Test
    internal fun canSerializeMapOfSizeOne_RecRef() {
        val mapRecursiveType = mapper.typeFactory.constructMapType(
            HashMap::class.java,
            String::class.java,
            UselessRecursiveObject::class.java
        )

        val parent = ObjectEditorController(mapRecursiveType).root as MapClassBuilder
        assertNotNull(parent)

        val entry0 =
            parent.createChild("0".toCb(), item = TreeItem()) as ComplexClassBuilder

        val key0 =
            "0".toCb(keyCb, entry0, entry0.propInfo[ENTRY_KEY], item = entry0.item.findChild(keyCb))
        key0.item.value = FilledClassBuilderNode(keyCb, key0, entry0)
        entry0[keyCb] = key0

        val value0 = ComplexClassBuilder(
            UselessRecursiveObject::class.type(),
            valueCb,
            entry0,
            entry0.propInfo[ENTRY_VALUE],
            item = entry0.item.findChild(valueCb)
        )
        value0.serObject[UselessRecursiveObject::with.name] = value0
        value0.item.value = FilledClassBuilderNode(valueCb, value0, entry0)
        entry0[valueCb] = value0

        //////////////////
        // second entry //
        //////////////////

        val entry1 =
            parent.createChild("1".toCb(), item = TreeItem()) as ComplexClassBuilder

        val key1 =
            "1".toCb(keyCb, entry1, entry1.propInfo[ENTRY_KEY], item = entry1.item.findChild(keyCb))
        key1.item.value = FilledClassBuilderNode(keyCb, key1, entry1)
        entry1[keyCb] = key1

        val value1 = ReferenceClassBuilder(
            refKey = valueCb,
            refParent = entry0,
            key = valueCb,
            parent = entry1,
            item = entry1.item.findChild(valueCb)
        )
        value1.item.value = FilledClassBuilderNode(valueCb, value1, entry1)
        entry1[valueCb] = value1

        var map: Map<*, *>? = null
        assertDoesNotThrow {
            println("mapper.writeValueAsString(parent) = ${mapper.writeValueAsString(parent)}")
            map = parent.toObject() as Map<*, *>?
        }
        assertTrue(map!!["0"] === map!!["1"]) {
            "map 0: ${map!!["0"]}\n" +
                    "map 1: ${map!!["1"]}"
        }
    }


    @Test
    internal fun canLoadSerializedMapOfSizeZero() {
        val parent = ObjectEditorController(mapStrStrType, emptyMap<String, String>()).root as MapClassBuilder
        assertNotNull(parent)

        assertEquals(emptyMap<String, String>(), parent.toObject())
    }

    @Test
    internal fun canLoadSerializedMapOfSizeOne() {
        val realMap = HashMap<String, String>().also {
            it["test?"] = "test!"
        }

        val parent = ObjectEditorController(mapStrStrType, realMap).root as MapClassBuilder
        assertNotNull(parent)
        assertEquals(realMap, parent.toObject())
    }

    @Test
    internal fun canLoadSerializedMapOfSizeN() {
        val realMap = HashMap<String, String>().also {
            it["test?"] = "test!"
            it["test!"] = "test!!"
            it["yee"] = "haw"
        }

        val parent = ObjectEditorController(mapStrStrType, realMap).root as MapClassBuilder
        assertNotNull(parent)
        assertEquals(realMap, parent.toObject())
    }
}
