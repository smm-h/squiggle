package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanGetAtIndex
import ir.smmh.nile.verbs.CanIterateOverValuesInReverse
import kotlin.random.Random

/**
 * Ordered subtype of [KeySet] so that the keys can be [reversed], [shuffled],
 * [sortedBy] sorting functions, and [filteredBy] predicates.
 */
sealed interface OrderedKeySet<K : Any> : KeySet<K>, CanGetAtIndex<K>, CanIterateOverValuesInReverse<K> {

    fun getMutableCopy(changesToSize: Change = Change(), changesToOrder: Change = Change()): Mutable<K>

    fun reversed(): CanChangeOrder<K> =
        getMutableCopy().apply { reverse() }

    fun shuffled(random: Random = Random): CanChangeOrder<K> =
        getMutableCopy().apply { shuffle(random) }

    fun sortedBy(ascending: Boolean = true, sortingFunction: (K) -> Int): CanChangeOrder<K> =
        getMutableCopy().apply { sortBy(ascending, sortingFunction) }

    fun sortedByKeyHash(ascending: Boolean = true) =
        sortedBy(ascending) { it.hashCode() }

    fun filteredBy(predicate: (K) -> Boolean): KeySet<K> =
        getMutableCopy().apply { filterBy(predicate) }

    fun <T> filteredByColumn(column: Column<K, T>, predicate: (T?) -> Boolean) =
        filteredBy { predicate(column[it]) }

    fun <T> filteredByColumn(column: Column<K, T>, data: T?) =
        filteredBy { column[it] == data }

    fun <T> sortedByColumn(column: Column<K, T>, ascending: Boolean = true, sortingFunction: (T?) -> Int) =
        sortedBy(ascending) { sortingFunction(column[it]) }

    /**
     * Ordered and can change order, but cannot add/remove keys
     */
    interface CanChangeOrder<K : Any> : OrderedKeySet<K> {

        val changesToOrder: Change

        fun reverse()

        fun shuffle(random: Random = Random)

        fun sortBy(ascending: Boolean = true, sortingFunction: (K) -> Int)

        fun sortByKeyHash(ascending: Boolean = true) =
            sortBy(ascending) { it.hashCode() }

        fun <T> sortByColumn(column: Column<K, T>, ascending: Boolean = true, sortingFunction: (T?) -> Int) =
            sortBy(ascending) { sortingFunction(column[it]) }
    }

    /**
     * Ordered but cannot change order; can add/remove keys
     */
    interface CanChangeSize<K : Any> : OrderedKeySet<K>, KeySet.CanChangeSize<K>

    /**
     * Ordered and can change order; can add/remove keys
     */
    interface Mutable<K : Any> : CanChangeOrder<K>, CanChangeSize<K>
}