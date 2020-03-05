package no.uib.inf219.gui.backend.serializers

import com.fasterxml.jackson.databind.ser.BeanSerializer
import no.uib.inf219.extra.type
import no.uib.inf219.gui.backend.ClassBuilder

/**
 * @author Elg
 */
class ClassBuilderSerializer : BeanSerializer(ClassBuilder::class.type(), null, null, null) {

}

