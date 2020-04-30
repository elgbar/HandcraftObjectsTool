package no.uib.inf219.gui.backend.cb.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.jsontype.TypeSerializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.cb.api.ClassBuilder
import kotlin.reflect.KClass

/**
 * @author Elg
 */
abstract class AbstractClassBuilderSerializer<T : ClassBuilder>(clazz: KClass<T>) : StdSerializer<T>(clazz.type()) {

    override fun serialize(value: T, gen: JsonGenerator, provider: SerializerProvider) {
        serializeMaybeWithType(value, gen, provider, null)
    }

    override fun serializeWithType(
        value: T,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    ) {
        serializeMaybeWithType(value, gen, provider, typeSer)
    }

    abstract fun serializeMaybeWithType(
        cb: T,
        gen: JsonGenerator,
        provider: SerializerProvider,
        typeSer: TypeSerializer?
    )
}
