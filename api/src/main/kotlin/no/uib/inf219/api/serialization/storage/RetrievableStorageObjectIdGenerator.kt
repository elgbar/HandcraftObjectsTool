package no.uib.inf219.api.serialization.storage

import com.fasterxml.jackson.annotation.ObjectIdGenerator
import no.uib.inf219.api.serialization.Identifiable

/**
 * @author Elg
 */
public class RetrievableStorageObjectIdGenerator<I, R : Identifiable<I>>(private val _scope: Class<Identifiable<*>>) :
    ObjectIdGenerator<R>() {


    constructor() : this(Identifiable::class.java) {}

    private val serialVersionUID = 1L


    @Transient
    private var store: RetrievableStorage<I, R> = StoreHandler.getStore(_scope) as RetrievableStorage<I, R>

    override fun forScope(scope: Class<*>): RetrievableStorageObjectIdGenerator<I, R> {
        return this
    }

    override fun newForSerialization(context: Any?): RetrievableStorageObjectIdGenerator<I, R> {
        return this
    }

    override fun key(key: Any?): IdKey? {
        return key?.let { IdKey(javaClass, _scope, it) }
    }

    override fun generateId(forPojo: Any?): R? {
        if (forPojo == null || !_scope.isAssignableFrom(forPojo::class.java)) {
            return null
        }
        val idf: Identifiable<I> = forPojo as Identifiable<I>
        return store.tryRetrieve(idf.getId())
    }

    override fun getScope(): Class<*> {
        return _scope
    }

    override fun canUseFor(gen: ObjectIdGenerator<*>?): Boolean {
        return false
    }

}
