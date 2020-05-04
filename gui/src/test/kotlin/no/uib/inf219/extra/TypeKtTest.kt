package no.uib.inf219.extra

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.framework.junit5.ApplicationExtension

@ExtendWith(ApplicationExtension::class)
internal class TypeKtTest {

    @Test
    fun primitiveArraysIsObjArrays_int() {
        assertEquals(Array<Int>::class.type(), IntArray::class.type())
    }


    @Test
    fun primitiveIsObj_int() {
        assertEquals(Int::class.javaPrimitiveType!!.type(), Int::class.javaObjectType.type())
    }
}
