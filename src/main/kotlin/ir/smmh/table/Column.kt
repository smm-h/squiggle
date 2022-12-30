package ir.smmh.table

import ir.smmh.nile.verbs.*

interface Column<K : Any, V> : CanGetAtPlace<K, V>, CanIterateOverValues<V?>, CanContainValue<V> {
    operator fun get(key: K) = getAtPlace(key)

    interface Mutable<K : Any, T> : Column<K, T>, CanSwapAtPlaces<K, T>, CanUnsetAtPlace<K> {
        operator fun set(key: K, value: T) = setAtPlace(key, value)
    }
}