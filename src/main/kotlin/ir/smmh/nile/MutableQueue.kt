package ir.smmh.nile


import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * An [Order] with a [Change] backed by a Java queue
 */
class MutableQueue<T>(
    private val queue: Queue<T> = ConcurrentLinkedQueue(),
    override val changesToSize: Change = Change(),
) : Order<T> {

    override val size get() = queue.size

    override fun iterator() = queue.iterator()

    override fun pollNullable() = if (queue.isEmpty()) null else {
        changesToSize.beforeChange()
        val data = queue.poll()
        changesToSize.afterChange()
        data
    }

    override fun intersect(other: Iterable<T>) = queue.toMutableSet().apply { retainAll(other) }

    override fun peekNullable(): T? = queue.peek()

    override fun canEnter() = true

    override fun enter(toEnter: T) {
        changesToSize.beforeChange()
        queue.add(toEnter)
        changesToSize.afterChange()
    }

    override fun clear() {
        if (queue.isNotEmpty()) {
            changesToSize.beforeChange()
            queue.clear()
            changesToSize.afterChange()
        }
    }

    override fun toString() = queue.toString()
}