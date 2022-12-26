package ir.smmh.nile

import ir.smmh.nile.verbs.CanClear
import java.util.*

/**
 * A [HasSize] whose elements [enter] and exit ([poll]) it with a predefined
 * order, much like a priority queue.
 */
interface Order<T> : HasSize, CanClear, Iterable<T> {
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

        fun <T> by(order: (T) -> Int) = by(Change(), order)
        fun <T> by(comparator: Comparator<T>) = by(Change(), comparator)
        fun <T> by(change: Change, order: (T) -> Int) = by(change, Comparator.comparingInt(order))
        fun <T> by(change: Change, comparator: Comparator<T>): Order<T> =
            MutableQueue(PriorityQueue(comparator), change)
    }
}
