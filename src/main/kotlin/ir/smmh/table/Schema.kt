package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanClear
import ir.smmh.nile.verbs.CanIterateOverValues
import ir.smmh.nile.verbs.CanIterateOverValuesMutably

interface Schema<K : Any, V> : CanIterateOverValues<Column<K, out V>> {

    fun row(key: K): Map<Column<K, out V>, V> =
        overValues.associateWith { it[key] }

    interface CanChangeValuesInColumns<K : Any, V> : Schema<K, V> {
        override val overValues: Iterable<Column.Mutable<K, out V>>
    }

    interface CanChangeColumns<K : Any, V> : Schema<K, V>, CanIterateOverValuesMutably<Column<K, out V>>, CanClear {
        fun <T : V> createColumnIn(changesToValues: Change = Change()): Column<K, T>
    }

    interface CanChangeColumnsAndValuesInThem<K : Any, V> : CanChangeValuesInColumns<K, V>, CanChangeColumns<K, V> {
        override fun <T : V> createColumnIn(changesToValues: Change): Column.Mutable<K, T>
    }
}