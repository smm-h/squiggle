package ir.smmh.nile

import ir.smmh.nile.verbs.CanAddTo
import ir.smmh.nile.verbs.CanClear
import ir.smmh.nile.verbs.CanContainValue
import ir.smmh.nile.verbs.CanRemoveElementFrom
import kotlin.collections.MutableCollection

/**
 * A Nilic collection with [Change], backed by a Java collection
 * @see nile.MutableCollection
 */
class NilizedCollection<T>(
    private val collection: MutableCollection<T>,
    override val changesToSize: Change = Change(),
) : CanAddTo<T>, CanRemoveElementFrom<T>, CanContainValue<T>, CanClear,
    CanIterateOverValues<T>, CanIterateOverValuesInReverse<T> {

    override fun add(toAdd: T) {
        changesToSize.beforeChange()
        if (collection.add(toAdd))
            changesToSize.afterChange()
    }

    override fun removeElementFrom(toRemove: T) {
        changesToSize.beforeChange()
        if (collection.remove(toRemove))
            changesToSize.afterChange()
    }

    override fun clear() {
        if (!isEmpty()) {
            changesToSize.beforeChange()
            collection.clear()
            changesToSize.afterChange()
        }
    }

    override val size: Int get() = collection.size
    override fun containsValue(toCheck: T) = collection.contains(toCheck)
    override fun isEmpty() = collection.isEmpty()

    override val overValues: Iterable<T> = collection
    override val overValuesInReverse: Iterable<T> by lazy { collection.reversed() }
}
