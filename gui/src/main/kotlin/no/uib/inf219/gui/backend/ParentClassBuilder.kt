package no.uib.inf219.gui.backend

import com.fasterxml.jackson.databind.JavaType

/**
 * TODO move all the overwritten methods here from ClassBuilder
 *
 * @author Elg
 */
abstract class ParentClassBuilder<out T> : ClassBuilder<T> {

//    init {
//        TODO("Check if our parent is a ParentClassBuilder")
//    }

    override fun isLeaf() = false

    override fun getChild(key: ClassBuilder<*>): ClassBuilder<*> {
        TODO()
    }

    //TODO make this return non-null
    override fun createClassBuilderFor(key: ClassBuilder<*>, init: ClassBuilder<*>?): ClassBuilder<*>? {
        TODO("not implemented")
    }

    override fun getSubClassBuilders(): Map<ClassBuilder<*>, ClassBuilder<*>?> {
        TODO("not implemented")
    }

    override fun getChildType(cb: ClassBuilder<*>): JavaType? {
        TODO("not implemented")
    }

    override fun resetChild(key: ClassBuilder<*>, element: ClassBuilder<*>?, restoreDefault: Boolean) {
        TODO("not implemented")
    }

    override fun getChildren(): List<ClassBuilder<*>> {
        return super.getChildren()
    }
}
