package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ClassBuilder

/**
 * @author Elg
 */
class ClassBuilderSerializer : StdSerializer<ClassBuilder<*>>(ClassBuilder::class.type()) {


    override fun serialize(value: ClassBuilder<*>, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeObject(value.serObject)
    }
}

