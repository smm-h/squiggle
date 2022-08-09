package ir.smmh.nile

import ir.smmh.nile.verbs.CanAddTo
import ir.smmh.nile.verbs.CanClear
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.nile.verbs.CanRemoveElementFrom
import kotlin.collections.MutableCollection

/**
 * A Nilic collection with a Mut backed by a Java collection
 * @see nile.MutableCollection
 */
class NilizedCollection<T>(
    private val collection: MutableCollection<T>,
    override val mut: Mut = Mut(),
) : CanAddTo<T>, CanRemoveElementFrom<T>, CanContainValue<T>, CanClear, Iterable<T>, Mut.Able {

    override fun add(toAdd: T) {
        mut.preMutate()
        if (collection.add(toAdd))
            mut.mutate()
    }

    override fun removeElementFrom(toRemove: T) {
        mut.preMutate()
        if (collection.remove(toRemove))
            mut.mutate()
    }

    override fun clear() {
        if (!isEmpty()) {
            mut.preMutate()
            collection.clear()
            mut.mutate()
        }
    }

    override val size: Int get() = collection.size
    override fun containsValue(toCheck: T) = collection.contains(toCheck)
    override fun isEmpty() = collection.isEmpty()
    override fun iterator() = collection.iterator()
}
