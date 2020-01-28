package no.uib.inf219.gui.view

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.io.SegmentedStringWriter
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
import com.fasterxml.jackson.databind.ser.PropertyWriter
import javafx.scene.control.TreeItem
import no.uib.inf219.api.serialization.SerializationManager
import no.uib.inf219.gui.controllers.ObjectEditorController
import tornadofx.*

/**
 * @author Elg
 */
class NodeExplorerView(val controller: ObjectEditorController) : View("Tree Explorer") {
    override val root = treeview<Any> {
        root = TreeItem(controller.serializer)
        root.isExpanded = true
        cellFormat {
            text = when (it) {
                is JsonSerializer<*> -> it.handledType().name
                is BeanProperty -> it.name
                else -> "Unknown: $it"
            }
            onDoubleClick {
                //create edit view for simple
                //add elem to collection for collection

                when (it) {
                    is JsonSerializer<*> -> controller.set(it.handledType())
                    is BeanProperty -> controller.set(it.type.rawClass)
                }
            }
        }
        populate { parent ->

            when (parent.value) {
                is JsonSerializer<*> -> serToIter(parent.value as JsonSerializer<*>)
                is BeanProperty -> serFromBean(parent.value as BeanProperty)
                else -> throw IllegalStateException("Unknown value: ${parent.value.javaClass}")
            }
        }
    }

    private fun serToIter(ser: JsonSerializer<*>): Iterable<PropertyWriter> {
        return Iterable { ser.properties() }
    }

    fun serFromBean(bean: BeanProperty): Iterable<BeanProperty> {
        val jfac = JsonFactory.builder().build()
        val gen: JsonGenerator = jfac.createGenerator(SegmentedStringWriter(jfac._getBufferRecycler()))

        val cfg: SerializationConfig = SerializationManager.mapper.serializationConfig
        cfg.initialize(gen)

        val ser: DefaultSerializerProvider =
            DefaultSerializerProvider.Impl().createInstance(cfg, SerializationManager.mapper.serializerFactory)
        return serToIter(ser.findTypedValueSerializer(bean.type, true, null))
    }

//        treeview<Pair<PartComp?, AttributeComp?>> {
//            root = TreeItem(Pair(controller.part, controller.attr))
//            root.isExpanded = true
//            cellFormat {
//                val (part, attr) = it
//                val fullPath = attr?.path ?: part?.className ?: "Failed to find any path or classname"
//                text = fullPath.substring(fullPath.lastIndexOf('.') + 1)
//                tooltip(
//                    attr?.className ?: part?.className
//                    ?: "Does your working directory contain the metadata?)"
//                )
//
//                //select the clicked node as currently editing
//                onDoubleClick {
//                    controller.attr = it.second
//                    controller.part = it.first
//
//                }
//
////                    action {
////                        replaceWith<PageTwo>(ViewTransition.Slide(0.3.seconds, Direction.LEFT)
////                    }
//            }
//            populate { parent ->
//                val (part, attr) = parent.value;
//
//                if (attr != null && attr.isList && DataManager.PARTS.containsKey(attr.className)) {
//                    val pp = DataManager.PARTS[attr.className]!!
//                    DataManager.findChildren(pp)
//                } else if (part != null) {
//                    DataManager.findChildren(part)
//                } else {
//                    //normal attribute
//                    null
//                }
//            }
//        }

}
