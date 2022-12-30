package ir.smmh.table

import ir.smmh.nile.verbs.*

interface KeySet<K : Any> : CanContainValue<K>, CanIterateOverValues<K> {
    // TODO CanClone<KeySet>

    interface CanChangeSize<K : Any> : KeySet<K>, CanAddTo<K>, CanClear, CanIterateOverValuesMutably<K> {

        fun filterBy(predicate: (K) -> Boolean)

        fun <T> filterByColumn(column: Column<K, T>, predicate: (T?) -> Boolean) =
            filterBy { predicate(column[it]) }

        fun <T> filterByColumn(column: Column<K, T>, data: T?) =
            filterBy { column[it] == data }
    }
}