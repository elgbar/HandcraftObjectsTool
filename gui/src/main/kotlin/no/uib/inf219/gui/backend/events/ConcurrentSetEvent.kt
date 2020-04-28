package no.uib.inf219.gui.backend.events

import kotlinx.event.AbstractEvent
import java.util.concurrent.ConcurrentHashMap

open class ConcurrentSetEvent<T> protected constructor(protected val backing: MutableSet<(T) -> Unit>) :
    AbstractEvent<T>(), MutableCollection<(T) -> Unit> by backing {

    constructor() : this(ConcurrentHashMap.newKeySet<(T) -> Unit>())

}
