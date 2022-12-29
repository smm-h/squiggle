package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanClear
import ir.smmh.nile.verbs.CanIterateOverValues
import ir.smmh.nile.verbs.CanIterateOverValuesAndRemove

interface Schema<K : Any, V> : CanIterateOverValues<Column<K, *>> {

    interface CanChangeValues<K : Any, V> : Schema<K, V> {
        override val overValues: Iterable<Column.Mutable<K, *>>
    }

    interface CanChangeSize<K : Any, V> : Schema<K, V>, CanIterateOverValuesAndRemove<Column<K, *>>, CanClear {
        fun <T : V> createColumnIn(changesToValues: Change = Change()): Column<K, T>
    }

    interface Mutable<K : Any, V> : CanChangeValues<K, V>, CanChangeSize<K, V> {
        override fun <T : V> createColumnIn(changesToValues: Change): Column.Mutable<K, T>
    }
}