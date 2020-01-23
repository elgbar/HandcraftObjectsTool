package no.uib.inf219.gui.controllers

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.io.SegmentedStringWriter
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.type.TypeFactory
import no.uib.inf219.gui.view.types.TypeResolver
import tornadofx.Controller
import no.uib.inf219.api.serialization.SerializationManager as SerMan


/**
 * @author Elg
 */
class ObjectEditorController(var clazz: Class<*>) : Controller() {

    lateinit var javaType: JavaType
    lateinit var serializer: JsonSerializer<Any>
    var bean: BeanProperty? = null
    val props: MutableMap<String, Any> = HashMap()

    init {
        set(clazz)
    }

    fun set(x: Class<*>) {
        clazz = x
        val tfac: TypeFactory = TypeFactory.defaultInstance()//.withClassLoader(cl)
        javaType = tfac.constructType(clazz)
        val jfac = JsonFactory.builder().build()
        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))

        val cfg: SerializationConfig = SerMan.mapper.serializationConfig

        cfg.initialize(gen)

        val ser: DefaultSerializerProvider =
            DefaultSerializerProvider.Impl().createInstance(cfg, SerMan.mapper.serializerFactory)
        serializer = ser.findTypedValueSerializer(javaType, true, null)

//        val schema = JsonSchemaGenerator(SerMan.mapper).generateSchema(clazz)
//        println(
//            "JsonSchemaGenerator(SerializationManager.mapper).generateSchema(clazz) = ${SerMan.dump(schema)}"
//        )

//        ob.props["response"] = "test"
//        ob.props["name"] = "test"
//
//        println("ob.props = ${ob.props}")
//
//        val o = ob.toObject()
//        println(SerMan.dumpMap<GuiMain>(ob.props))
////        ob.props["conv"] = "test"
////        ob.props["prerequisites"] =

        val tr = TypeResolver.resolve(String::class.java)
        println("tr = ${tr}")


    }

    fun serialize(): Any {
        return SerMan.dump(SerMan.loadFromMap(props, clazz))
    }
}
