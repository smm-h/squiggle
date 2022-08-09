package ir.smmh.util

import ir.smmh.nile.Multitude
import ir.smmh.nile.NilizedCollection
import org.jetbrains.annotations.Contract
import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier

object FunctionalUtil {

    fun interface ToStringFunction<T> {
        fun applyAsString(`object`: T): String?
    }

    @Contract("_->_")
    fun <T> itself(it: T) = it

    fun <T> with(it: T?, defaultValue: T): T {
        return it ?: defaultValue
    }

    fun <T> with(it: T?, supplier: Supplier<out T>): T {
        return it ?: supplier.get()
    }

    fun <T> with(it: T?, consumer: Consumer<T>) {
        if (it != null) consumer.accept(it)
    }

    @Contract("_, _, !null -> !null")
    fun <T, R> with(it: T?, function: Function<in T?, out R>, defaultValue: R?): R? {
        return if (it == null) defaultValue else function.apply(it)
    }

    fun <T> `is`(o: T, p: (T) -> Boolean): Boolean {
        return p(o)
    }

    fun <T> isNot(o: T, p: (T) -> Boolean): Boolean {
        return !p(o)
    }

    fun <T, R> Iterator<T>.map(transform: (T) -> R): Iterator<R> {
        val outer = this
        return object : Iterator<R> {
            override fun hasNext() = outer.hasNext()
            override fun next() = transform(outer.next())
        }
    }

    operator fun <T> ((T) -> Boolean).not(): (T) -> Boolean {
        return { !this(it) }
    }

    infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean {
        return { this(it) && other(it) }
    }

    infix fun <T> ((T) -> Boolean).or(other: (T) -> Boolean): (T) -> Boolean {
        return { this(it) || other(it) }
    }

    fun <T : Comparable<T>?> sort(input: Iterable<T>): Iterable<T> {
        val list: MutableList<T> = ArrayList(capacityNeededFor(input, 10))
        for (i in input) with(i, Consumer<T> { e: T -> list.add(e) })
        Collections.sort(list)
        return list
    }

    fun capacityNeededFor(iterable: Iterable<*>?): Int? {
        return when (iterable) {
            is NilizedCollection<*> -> iterable.size
            is Multitude -> iterable.size
            else -> null
        }
    }

    fun capacityNeededFor(iterable: Iterable<*>?, defaultCapacity: Int): Int {
        val size = capacityNeededFor(iterable)
        return size ?: defaultCapacity
    }

    fun capacityNeededForIterateIfNull(iterable: Iterable<*>): Int {
        val size = capacityNeededFor(iterable)
        return if (size == null) {
            var count = 0
            for (i in iterable) {
                count++
            }
            count
        } else {
            size
        }
    }

    fun isEmpty(iterable: Iterable<*>): Boolean {
        val size = capacityNeededFor(iterable)
        return if (size == null) {
            !iterable.iterator().hasNext()
        } else {
            size == 0
        }
    }

    fun <T, R> convert(input: Iterable<T>, convertor: Function<T, R>): Iterable<R> {
        val list: MutableList<R> = ArrayList(capacityNeededFor(input, 10))
        for (i in input) with(convertor.apply(i), Consumer<R> { e: R -> list.add(e) })
        return list
    }

    inline fun <I, O> Iterable<I>.toSet(transform: (I) -> O): Set<O> {
        val destination = HashSet<O>()
        for (element in this) destination.add(transform(element))
        return destination
    }
}