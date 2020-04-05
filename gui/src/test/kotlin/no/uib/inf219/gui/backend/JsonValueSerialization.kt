package no.uib.inf219.gui.backend

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import javafx.scene.control.TreeItem
import no.uib.inf219.extra.toCb
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.simple.UUIDClassBuilder
import no.uib.inf219.gui.loader.ClassInformation
import no.uib.inf219.gui.view.ControlPanelView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension
import java.util.*

/**
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
class JsonValueSerialization {

    class JsonValueExample @JsonCreator constructor(uid: UUID?) {

        var uid = uid
            @JsonValue get

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is JsonValueExample) return false
            if (uid != other.uid) return false
            return true
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    class TypedJsonValueExample @JsonCreator constructor(uid: UUID?) {

        var uid = uid
            @JsonValue get

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is JsonValueExample) return false
            if (uid != other.uid) return false
            return true
        }
    }

    @Test
    internal fun serializeJsonValue() {

        val uuid: UUID = UUID.nameUUIDFromBytes(byteArrayOf(16))

        val jsonValueInstance = JsonValueExample(uuid)
        val expected = ControlPanelView.mapper.writeValueAsString(jsonValueInstance)
        println("expected = $expected")

        val cb = ComplexClassBuilder(
            JsonValueExample::class.type(),
            key = "key".toCb(),
            parent = SimpleClassBuilder.FAKE_ROOT,
            item = TreeItem()
        ).apply {
            serObject[ClassInformation.VALUE_DELEGATOR_NAME] = UUIDClassBuilder(
                uuid,
                key = "key".toCb(),
                parent = SimpleClassBuilder.FAKE_ROOT,
                item = TreeItem()
            )
        }

        val json = ControlPanelView.mapper.writeValueAsString(cb)
        println("got = $json")

        val obj = cb.toObject()

        assertEquals(expected, json)
        assertEquals(jsonValueInstance, obj)
    }

    @Test
    internal fun serializeNullJsonValue() {

        val jsonValueInstance = JsonValueExample(null)

        val expectedJson = ControlPanelView.mapper.writeValueAsString(jsonValueInstance)
        println("expected = $expectedJson")
        val expectedObj = ControlPanelView.mapper.readValue(expectedJson, JsonValueExample::class.java)

        val cb = ComplexClassBuilder(
            JsonValueExample::class.type(),
            key = "key".toCb(),
            parent = SimpleClassBuilder.FAKE_ROOT,
            item = TreeItem()
        ).apply {
            serObject[ClassInformation.VALUE_DELEGATOR_NAME] = null
        }

        val json = ControlPanelView.mapper.writeValueAsString(cb)
        println("got = $json")

        val obj = cb.toObject()

        assertEquals(expectedJson, json)
        assertEquals(expectedObj, obj)
    }

    @Disabled //FIXME!!
    @Test
    internal fun serializeTypedJsonValue() {

        val uuid: UUID = UUID.nameUUIDFromBytes(byteArrayOf(16))

        val jsonValueInstance = TypedJsonValueExample(uuid)

        val expected = ControlPanelView.mapper.writeValueAsString(jsonValueInstance)
        println("expected = $expected")

        val cb = ComplexClassBuilder(
            TypedJsonValueExample::class.type(),
            key = "key".toCb(),
            parent = SimpleClassBuilder.FAKE_ROOT,
            item = TreeItem()
        ).apply {
            serObject[ClassInformation.VALUE_DELEGATOR_NAME] = UUIDClassBuilder(
                uuid,
                key = "key".toCb(),
                parent = SimpleClassBuilder.FAKE_ROOT,
                item = TreeItem()
            )
        }

        val json = ControlPanelView.mapper.writeValueAsString(cb)
        println("got = $json")

        val obj = cb.toObject()

        assertEquals(expected, json)
        assertEquals(jsonValueInstance, obj)
    }


    ///////////////////////////////////////////////////////////////////////////
    // Using JsonIdentityInfo and JsonValue seems to be incompatible         //
    // But it might also be a bug in Jackson, so keep this test just in case //
    ///////////////////////////////////////////////////////////////////////////


//    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator::class)
//    class IdJsonValueExample @JsonCreator constructor(uid: UUID?) {
//
//        var uid = uid
//            @JsonValue get
//
//        override fun equals(other: Any?): Boolean {
//            if (this === other) return true
//            if (other !is JsonValueExample) return false
//            if (uid != other.uid) return false
//            return true
//        }
//    }
//
//    @Test
//    internal fun serializeIdJsonValue() {
//
//        val uuid: UUID = UUID.nameUUIDFromBytes(byteArrayOf(16))
//
//        val jsonValueInstance = IdJsonValueExample(uuid)
//
//        val expectedJson = ControlPanelView.mapper.writeValueAsString(jsonValueInstance)
//        println("expected = $expectedJson")
//        val expectedObj = ControlPanelView.mapper.readValue(expectedJson, IdJsonValueExample::class.java)
//
//        val cb = ComplexClassBuilder(IdJsonValueExample::class.type()).apply {
//            serObject[ClassInformation.VALUE_DELEGATOR_NAME] = UUIDClassBuilder(uuid)
//        }
//
//        val json = ControlPanelView.mapper.writeValueAsString(cb)
//        println("got = $json")
//
//        val obj = cb.toObject()
//
//        Assertions.assertEquals(expectedJson, json)
//        Assertions.assertEquals(expectedObj, obj)
//    }
}
