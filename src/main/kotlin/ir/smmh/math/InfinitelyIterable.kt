package ir.smmh.math

fun interface InfinitelyIterable<out T> : Iterable<T> {
    override fun iterator(): Iterator<T>

    fun interface Iterator<out T> : kotlin.collections.Iterator<T> {
        override fun hasNext() = true
    }
}