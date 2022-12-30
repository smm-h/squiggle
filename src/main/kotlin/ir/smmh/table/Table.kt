package ir.smmh.table

import ir.smmh.nile.verbs.CanAddTo
import ir.smmh.nile.verbs.CanClear
import ir.smmh.nile.verbs.CanRemoveElementFrom

/**
 * A not-necessarily ordered set of keys of non-nullable type [K], paired with
 * a collection of [Column]s that map those keys to values of type, specified by
 * the column.
 */
interface Table<K : Any, V> { // TODO CanClone<Table>
    val schema: Schema<K, V>
    val keySet: KeySet<K>

    interface CanChangeValuesInCells<K : Any, V> : Table<K, V> {
        override val schema: Schema.CanChangeValuesInColumns<K, V>
    }

    interface CanChangeSizeOfRows<K : Any, V> : Table<K, V>, CanAddTo<K>, CanRemoveElementFrom<K>, CanClear {
        override val keySet: KeySet.CanChangeSize<K>
    }

    interface CanChangeSizeOfColumns<K : Any, V> : Table<K, V> {
        override val schema: Schema.CanChangeColumns<K, V>
    }

    interface CanChangeEverything<K : Any, V> : CanChangeValuesInCells<K, V>, CanChangeSizeOfColumns<K, V> {
        override val schema: Schema.CanChangeColumnsAndValuesInThem<K, V>
    }
}