package no.uib.inf219.gui.backend.events

import no.uib.inf219.gui.controllers.classBuilderNode.ClassBuilderNode

fun <T> concurrentEvent() = ConcurrentSetEvent<T>()

val resetEvent = concurrentEvent<ClassBuilderResetEvent>()

data class ClassBuilderResetEvent(val cbn: ClassBuilderNode, val restoreDefault: Boolean)
