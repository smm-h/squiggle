package ir.smmh.nile

/**
 * Circular fixed-size contiguous queue of non-null elements
 *
 * @param <T> type of data
 */
class ArrayQueue<T>(capacity: Int, override val mut: Mut = Mut()) : Order<T>, Mut.Able {
    @Suppress("UNCHECKED_CAST")
    private val array = arrayOfNulls<Any>(capacity) as Array<T>
    override var size = 0
        private set

    private var head = 0
    private var tail = 0

    override fun iterator() = TODO("ArrayQueue.iterator")
    override fun intersect(other: Iterable<T>) = TODO("ArrayQueue.intersect")

    @Synchronized
    override fun pollNullable(): T? {
        return if (size > 0) {
            mut.preMutate()
            val data = array[tail++]
            size--
            if (tail >= array.size) tail = 0
            mut.mutate()
            data
        } else null
    }

    override fun peekNullable() = if (size > 0) array[tail] else null

    override fun canEnter() = size < array.size

    @Synchronized
    override fun enter(toEnter: T) {
        if (canEnter()) {
            mut.preMutate()
            array[head++] = toEnter
            if (head >= array.size) head = 0
            size++
            mut.mutate()
        }
    }

    override fun clear() {
        mut.preMutate()
        head = 0
        tail = 0
        size = 0
        mut.mutate()
    }
}