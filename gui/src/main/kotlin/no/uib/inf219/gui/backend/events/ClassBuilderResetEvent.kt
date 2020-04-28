package no.uib.inf219.gui.backend.events

import kotlinx.event.event
import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode


val resetEvent = event<ClassBuilderResetEvent>()

data class ClassBuilderResetEvent(val cbn: ClassBuilderNode, val restoreDefault: Boolean)


