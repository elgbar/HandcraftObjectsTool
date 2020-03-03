package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.databind.BeanDescription
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializationConfig
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier

class ClassBuilderBeanSerializerModifier : BeanSerializerModifier() {

    override fun modifySerializer(
        config: SerializationConfig?,
        beanDesc: BeanDescription?,
        serializer: JsonSerializer<*>
    ): JsonSerializer<*> {
//        if (beanDesc?.beanClass == ClassBuilder::class.java) {
//            return ClassBuilderSerializer(serializer as JsonSerializer<Any>)
//        }
        return serializer
    }
}
