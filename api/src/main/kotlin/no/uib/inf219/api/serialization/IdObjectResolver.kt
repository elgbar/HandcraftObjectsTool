package no.uib.inf219.api.serialization

import com.fasterxml.jackson.annotation.ObjectIdGenerator
import com.fasterxml.jackson.annotation.ObjectIdResolver
import no.uib.inf219.api.serialization.storage.StoreHandler

/**
 * @author Elg
 */
class IdObjectResolver : ObjectIdResolver {

    override fun resolveId(id: ObjectIdGenerator.IdKey?): Any? {
        val store = StoreHandler.getStore(id!!.type)
//        return store.tryRetrieve(id.key)
        TODO("not implemented")
    }

    override fun newForDeserialization(context: Any?): ObjectIdResolver {
        TODO("not implemented")
    }

    override fun bindItem(id: ObjectIdGenerator.IdKey?, pojo: Any?) {
        TODO("not implemented")
    }

    override fun canUseFor(resolverType: ObjectIdResolver?): Boolean {
        TODO("not implemented")
    }
}
