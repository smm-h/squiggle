package ir.smmh.table

import ir.smmh.nile.verbs.*


interface Table<K : Any> { // TODO CanClone<Table>
    val schema: Schema<K, *>
    val keySet: KeySet<K>

    interface CanChangeValues<K : Any> : Table<K>, CanAddTo<K>, CanRemoveElementFrom<K>, CanClear {
        override val schema: Schema.CanChangeValues<K, *>
        override val keySet: KeySet.CanChangeSize<K>
    }

    interface CanChangeSchema<K : Any> : Table<K> {
        override val schema: Schema.CanChangeSize<K, *>
    }

    interface Mutable<K : Any> : CanChangeValues<K>, CanChangeSchema<K> {
        override val schema: Schema.Mutable<K, *>
    }
}