package ir.smmh.nile

/**
 * A Java collection with a [Change], backed by a Java collection
 * @see nile.NilizedCollection
 */
class MutableCollection<T>(
    private val collection: kotlin.collections.MutableCollection<T>,
    val change: Change = Change()
) : AbstractMutableCollection<T>() {

    override val size: Int get() = collection.size
    override fun isEmpty() = collection.isEmpty()
    override fun iterator() = collection.iterator()

    override fun add(element: T): Boolean {
        change.beforeChange()
        if (collection.add(element)) {
            change.afterChange()
            return true
        }
        return false
    }

    override fun addAll(elements: Collection<T>): Boolean {
        change.beforeChange()
        if (collection.addAll(elements)) {
            change.afterChange()
            return true
        }
        return false
    }

    override fun clear() {
        if (!isEmpty()) {
            change.beforeChange()
            collection.clear()
            change.afterChange()
        }
    }

    override fun remove(element: T): Boolean {
        change.beforeChange()
        if (collection.remove(element)) {
            change.afterChange()
            return true
        }
        return false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        change.beforeChange()
        if (collection.removeAll(elements.toSet())) {
            change.afterChange()
            return true
        }
        return false
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        change.beforeChange()
        if (collection.retainAll(elements.toSet())) {
            change.afterChange()
            return true
        }
        return false
    }
}