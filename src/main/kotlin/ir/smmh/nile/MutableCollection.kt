package ir.smmh.nile

/**
 * A Java collection with a Mut backed by a Java collection
 * @see nile.NilizedCollection
 */
class MutableCollection<T>(
    private val collection: kotlin.collections.MutableCollection<T>,
    override val mut: Mut = Mut()
) : AbstractMutableCollection<T>(), Mut.Able {

    override val size: Int get() = collection.size
    override fun isEmpty() = collection.isEmpty()
    override fun iterator() = collection.iterator()

    override fun add(element: T): Boolean {
        mut.preMutate()
        if (collection.add(element)) {
            mut.mutate()
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        mut.preMutate()
        if (collection.addAll(elements)) {
            mut.mutate()
            return true
        }
        return false
    }

    override fun clear() {
        if (!isEmpty()) {
            mut.preMutate()
            collection.clear()
            mut.mutate()
        }
    }

    override fun remove(element: T): Boolean {
        mut.preMutate()
        if (collection.remove(element)) {
            mut.mutate()
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        mut.preMutate()
        if (collection.removeAll(elements.toSet())) {
            mut.mutate()
            return true
        }
        return false
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        mut.preMutate()
        if (collection.retainAll(elements.toSet())) {
            mut.mutate()
            return true
        }
        return false
    }
}