package ir.smmh.util

object FunctionalUtil {

    operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean =
        { !this(it) }

    infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean =
        { this(it) && other(it) }

    infix fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean =
        { this(it) || other(it) }

    inline fun <I, O> Iterable<I>.toSet(transform: (I) -> O): Set<O> {
        val destination = HashSet<O>()
        for (element in this) destination.add(transform(element))
        return destination
    }
}