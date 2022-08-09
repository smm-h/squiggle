package ir.smmh.nile


import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * An `Order` with a Mut backed by a Java queue
 * @see nile.Order
 */
class MutableQueue<T>(
    private val queue: Queue<T> = ConcurrentLinkedQueue(),
    override val mut: Mut = Mut()
) : Order<T>, Mut.Able {

    override val size get() = queue.size

    override fun iterator() = queue.iterator()

    override fun pollNullable() = if (queue.isEmpty()) null else {
        mut.preMutate()
        val data = queue.poll()
        mut.mutate()
        data
    }

    override fun intersect(other: Iterable<T>) = queue.toMutableSet().apply { retainAll(other) }

    override fun peekNullable(): T? = queue.peek()

    override fun canEnter() = true

    override fun enter(toEnter: T) {
        mut.preMutate()
        queue.add(toEnter)
        mut.mutate()
    }

    override fun clear() {
        if (queue.isNotEmpty()) {
            mut.preMutate()
            queue.clear()
            mut.mutate()
        }
    }

    override fun toString() = queue.toString()
}