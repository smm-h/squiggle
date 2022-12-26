package ir.smmh.nile


/**
 * Fixed-size contiguous stack of non-null elements
 *
 * @param <T> type of data
 */
class ArrayStack<T>(capacity: Int, override val changesToSize: Change = Change()) : Order<T> {

    @Suppress("UNCHECKED_CAST")
    private val array = arrayOfNulls<Any>(capacity) as Array<T>
    override var size = 0
        private set

    override fun iterator() = TODO("ArrayStack.iterator")
    override fun intersect(other: Iterable<T>) = TODO("ArrayStack.intersect")

    @Synchronized
    override fun pollNullable(): T? {
        return if (size > 0) {
            changesToSize.beforeChange()
            val data = array[--size]
            changesToSize.afterChange()
            data
        } else {
            null
        }
    }

    override fun peekNullable() = if (size > 0) array[size - 1] else null
    override fun canEnter() = size < array.size

    @Synchronized
    override fun enter(toEnter: T) {
        if (canEnter()) {
            changesToSize.beforeChange()
            array[size++] = toEnter
            changesToSize.afterChange()
        }
    }

    override fun clear() {
        changesToSize.beforeChange()
        size = 0
        changesToSize.afterChange()
    }
}