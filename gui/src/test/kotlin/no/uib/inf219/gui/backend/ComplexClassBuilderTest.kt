package no.uib.inf219.gui.backend

import no.uib.inf219.extra.findChild
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import no.uib.inf219.gui.backend.cb.getClassBuilder
import no.uib.inf219.gui.backend.cb.parents.ComplexClassBuilder
import no.uib.inf219.gui.backend.cb.simple.StringClassBuilder
import no.uib.inf219.gui.backend.cb.toCb
import no.uib.inf219.gui.backend.cb.toObject
import no.uib.inf219.gui.controllers.ObjectEditorController
import no.uib.inf219.test.PrimitiveDefaultValueShowcase
import no.uib.inf219.test.conv.Conversation
import no.uib.inf219.test.precondition.AlwaysTruePrecondition
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

/**
 *
 *
 * @author Elg
 */
@ExtendWith(ApplicationExtension::class)
internal class ComplexClassBuilderTest {

    ////////////////////////
    // serialization test //
    ////////////////////////

    @Test
    internal fun canCreateAbstractTypes() {
        val created = ObjectEditorController(AlwaysTruePrecondition::class.type()).root.toObject()
        assertEquals(AlwaysTruePrecondition(), created)
    }

    @Test
    internal fun canCreateClassWithPrimitivesAndString() {
        val created = ObjectEditorController(PrimitiveDefaultValueShowcase::class.type()).root.toObject()
        assertEquals(PrimitiveDefaultValueShowcase(42, 46, 0.1, 0.1f, true, 6, 1, 'a', "abc"), created)
    }


    /////////////////////////
    // Creation of cb test //
    /////////////////////////

    @Test
    internal fun defaultValuesPresentAfterInit() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder
        val props = cb.propInfo
        for ((key, meta) in cb.propInfo) {
            assertTrue(cb.serObject.containsKey(key)) {
                "Complex class builder does not contain the expected key $key"
            }
            val def = meta.getDefaultInstance()
            if (def == null) {
                assertNull(cb.serObject[key])
            } else {
                assertNotNull(cb.serObject[key])


                val prop = props[key]
                assertNotNull(prop)

                val created = cb.getClassBuilder(prop!!.type, key.toCb(), def, prop) ?: fail()

                assertEquals(created, cb.serObject[key]) {
                    "Complex cb does not contain the correct default value for key '$key'"
                }
            }
        }
    }

    @Test
    internal fun resetChild_correctKey() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        val propKey = Conversation::name.name
        //make sure this test makes sense with a real property
        assertNotNull(cb.serObject[propKey]) { "Property key is wrong. Change it to one of ${cb.propInfo.keys}" }

        val prop: StringClassBuilder = cb.serObject[propKey] as StringClassBuilder
        val orgPropValue = prop.serObject
        prop.serObject = "something else!!"
        assertNotEquals(orgPropValue, prop.serObject)

        assertNotEquals(orgPropValue, cb.serObject[propKey]?.serObject) { "Reset value equal to initial value" }

        assertDoesNotThrow {
            cb.resetChild(propKey.toCb())
        }

        assertFalse(prop === cb.serObject[propKey]) { "property not replaced with another" }

        assertEquals(orgPropValue, cb.serObject[propKey]?.serObject) { "Reset value different from initial value" }
    }

    @Test
    internal fun resetChild_incorrectKey() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        val propKey = "name178238623"
        //make sure this test makes sense with a real property
        assertFalse(cb.serObject.containsKey(propKey)) { "Property key already exists, change propKey to a property that does not exists" }

        assertThrows(IllegalArgumentException::class.java) {
            cb.resetChild(propKey.toCb())
        }
    }

    @Test
    internal fun resetChild_incorrectValue() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        val propKey = cb.serObject.filterValues { it != null }.keys.first()
        val orgProp = cb.serObject[propKey]

        //this will discard orgProp and create a new one
        cb.resetChild(propKey.toCb(), restoreDefault = true)

        //we cannot reset the child with an old property
        assertThrows(IllegalArgumentException::class.java) {
            cb.resetChild(propKey.toCb(), orgProp)
        }

    }

    @Test
    internal fun resetChild_dontRestore_complex() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        val propKey = cb.serObject.filterValues { it != null }.keys.first()
        val propKeyCb = propKey.toCb()
        val orgProp = cb.serObject[propKey]

        assertNotNull(orgProp)
        val item = cb.item.findChild(propKeyCb) ?: fail("failed to find child $propKey of $cb")
        cb.resetChild(propKeyCb, restoreDefault = true)
        val newProp = cb.serObject[propKey]
        assertNotNull(item.value)
        assertTrue(item.value.cb === newProp)

        //default is equal but not same object
        assertEquals(orgProp, newProp)
        assertFalse(orgProp === newProp)

        assertNotNull(cb.serObject[propKey])
        cb.resetChild(propKeyCb, restoreDefault = false)

        assertNull(cb.serObject[propKey])
    }

    @Test
    internal fun createClassBuilderFor_invalidInit() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder
        val propKey = cb.serObject.filterValues { it == null }.keys.first()

        val invalid = 1.toCb()
        assertThrows(IllegalArgumentException::class.java) {
            cb.createChildClassBuilder(propKey.toCb(), invalid)
        }
    }

    @Test
    internal fun createClassBuilderFor_invalidKey() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder
        assertThrows(IllegalArgumentException::class.java) {
            cb.createChildClassBuilder("invalid key".toCb())
        }
    }

    @Test
    internal fun createClassBuilderFor_correctKeyNullInit() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder
        val propKey = cb.serObject.filterValues { it == null }.keys.first()

        var created: ClassBuilder? = null
        assertDoesNotThrow {
            created = cb.createChildClassBuilder(propKey.toCb(), null)
        }
        assertNotNull(created)
    }

    @Test
    internal fun createClassBuilderFor_validKeyExistingPropDoesNotGetOverWritten() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        val propKey = Conversation::name.name
        val propKeyCb = propKey.toCb()
        //make sure this test makes sense with a real property
        assertNotNull(cb.serObject[propKey]) { "Property key is wrong. Change it to one of ${cb.propInfo.keys}" }

        val init = cb.createChildClassBuilder(propKeyCb)
        assertNotNull(init)
        assertNotNull(cb.serObject[propKey])

        var created: ClassBuilder? = null
        assertDoesNotThrow {
            created = cb.createChildClassBuilder(propKeyCb, init)
        }
        assertTrue(init === cb.serObject[propKey])
        assertTrue(init === created)
    }

    @Test
    internal fun createClassBuilderFor_validKeyNonNullInit_propertyDoesExist() {
        val cb = ObjectEditorController(Conversation::class.type()).root as ComplexClassBuilder

        val propKey = cb.serObject.filterValues { it != null }.keys.first()

        val orgProp = cb.serObject[propKey]
        assertNotNull(orgProp)

        var created: ClassBuilder? = null

        assertDoesNotThrow {
            created = cb.createChildClassBuilder(key = propKey.toCb())
        }

        assertTrue(orgProp === created)
    }
}
