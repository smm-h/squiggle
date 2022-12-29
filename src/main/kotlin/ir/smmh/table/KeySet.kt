package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.*
import kotlin.random.Random

interface KeySet<K : Any> : CanContainValue<K>, CanIterateOverValues<K> {
    // TODO CanClone<KeySet>

    interface HasOrder<K : Any> : KeySet<K>, CanGetAtIndex<K>, CanIterateOverValuesInReverse<K> {

        fun reversed(): KeySet<K>
        fun shuffled(random: Random = Random): KeySet<K>
        fun sortedBy(ascending: Boolean = true, sortingFunction: (K) -> Int): KeySet<K>
        fun filteredBy(predicate: (K) -> Boolean): KeySet<K>

        fun sortedByKeyHash(ascending: Boolean = true) =
            sortedBy(ascending) { it.hashCode() }

        fun <T> sortedByColumn(column: Column<K, T>, ascending: Boolean = true, sortingFunction: (T?) -> Int) =
            sortedBy(ascending) { sortingFunction(column[it]) }

        fun <T> filteredByColumn(column: Column<K, T>, predicate: (T?) -> Boolean) =
            filteredBy { predicate(column[it]) }

        fun <T> filteredByColumn(column: Column<K, T>, data: T?) =
            filteredBy { column[it] == data }

        interface CanChangeOrder<K : Any> : HasOrder<K> {

            val changesToOrder: Change

            override fun reversed() =
                apply { reverse() }

            override fun shuffled(random: Random) =
                apply { shuffle(random) }

            override fun sortedBy(ascending: Boolean, sortingFunction: (K) -> Int) =
                apply { sortBy(ascending, sortingFunction) }

            fun reverse()
            fun shuffle(random: Random = Random)
            fun sortBy(ascending: Boolean = true, sortingFunction: (K) -> Int)

            fun sortByKeyHash(ascending: Boolean = true) =
                sortBy(ascending) { it.hashCode() }

            fun <T> sortByColumn(column: Column<K, T>, ascending: Boolean = true, sortingFunction: (T?) -> Int) =
                sortBy(ascending) { sortingFunction(column[it]) }
        }
    }

    interface CanChangeSize<K : Any> : KeySet<K>, CanAddTo<K>, CanClear, CanIterateOverValuesAndRemove<K> {

        fun filterBy(predicate: (K) -> Boolean)

        fun <T> filterByColumn(column: Column<K, T>, predicate: (T?) -> Boolean) =
            filterBy { predicate(column[it]) }

        fun <T> filterByColumn(column: Column<K, T>, data: T?) =
            filterBy { column[it] == data }
    }

    interface Mutable<K : Any> : CanChangeSize<K>, HasOrder.CanChangeOrder<K> {

        override fun filteredBy(predicate: (K) -> Boolean): KeySet<K> =
            apply { filterBy(predicate) }
    }
}