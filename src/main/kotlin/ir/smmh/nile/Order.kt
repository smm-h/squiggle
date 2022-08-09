package ir.smmh.nile

import ir.smmh.nile.verbs.CanClear
import java.util.*

/**
 * A [Multitude] whose elements [enter] and exit ([poll]) it with a predefined
 * order, much like a priority queue.
 */
interface Order<T> : Multitude, CanClear, Iterable<T> {
    fun peekNullable(): T?
    fun pollNullable(): T?
    fun peek(): T = peekNullable() ?: throw NullPointerException()
    fun poll(): T = pollNullable() ?: throw NullPointerException()
    fun canEnter(): Boolean
    fun enter(toEnter: T)
    fun enterAll(toEnter: Iterable<T>) {
        if (canEnter()) toEnter.forEach { enter(it) }
    }

    fun intersect(other: Iterable<T>): Iterable<T>

    companion object {
        val shortestFirst: (String) -> Int = { it.length }
        val longestFirst: (String) -> Int = { -it.length }

        fun <T> by(order: (T) -> Int) = by(Mut(), order)
        fun <T> by(comparator: Comparator<T>) = by(Mut(), comparator)
        fun <T> by(mut: Mut, order: (T) -> Int) = by(mut, Comparator.comparingInt(order))
        fun <T> by(mut: Mut, comparator: Comparator<T>): Order<T> =
            MutableQueue(PriorityQueue(comparator), mut)
    }
}
