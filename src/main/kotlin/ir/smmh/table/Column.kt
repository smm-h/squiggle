package ir.smmh.table

import ir.smmh.nile.verbs.*

interface Column<K : Any, T> : CanGetAtPlace<K, T>, CanIterateOverValues<T?>, CanContainValue<T> {
    operator fun get(key: K) = getAtPlace(key)

    interface Mutable<K : Any, T> : Column<K, T>, CanSwapAtPlaces<K, T>, CanUnsetAtPlace<K> {
        operator fun set(key: K, value: T) = setAtPlace(key, value)
    }
}