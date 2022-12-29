package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.*


interface Table<K : Any> { // TODO CanClone<Table>
    val schema: Schema<K, *>
    //val overColumns: Iterable<Table.Column<K, *>> get() = schema.overValues

    val keySet: KeySet<K>
    //val overRows: Iterable<K> get() = rows.overValues

    interface CanChangeValues<K : Any> : Table<K>, CanAddTo<K>, CanRemoveElementFrom<K>, CanClear {
        override val schema: Schema.CanChangeValues<K, *>
        //override val overColumns: Iterable<Table.Column.Mutable<K, *>> get() = schema.overValues

        override val keySet: KeySet.CanChangeSize<K>
        val overRowsMutably: MutableIterable<K> get() = keySet.overValuesMutably

        override val size get() = keySet.size

        fun add(key: K, toAdd: (K) -> Unit) {
            changesToSize.beforeChange()
            keySet.add(key)
            toAdd(key)
            changesToSize.afterChange()
        }

        override fun add(toAdd: K) {
            changesToSize.beforeChange()
            keySet.add(toAdd)
            changesToSize.afterChange()
        }

        override fun removeElementFrom(toRemove: K) {
            changesToSize.beforeChange()
            keySet.removeElementFrom(toRemove)
            schema.overValues.forEach { it.unsetAtPlace(toRemove) }
            changesToSize.afterChange()
        }

        override fun clear() {
            changesToSize.beforeChange()
            schema.overValues.forEach(CanUnsetAtPlace<K>::unsetAll)
            changesToSize.afterChange()
        }
    }

    interface CanChangeSchema<K : Any> : Table<K> {
        override val schema: Schema.CanChangeSchema<K, *>
        val overColumnsMutably: MutableIterable<Table.Column<K, *>> get() = schema.overValuesMutably
    }

    interface Mutable<K : Any> : CanChangeValues<K>, CanChangeSchema<K> {
        override val schema: Schema.Mutable<K, *>
    }

    interface Schema<K : Any, V> : CanIterateOverValues<Column<K, *>> {

        interface CanChangeValues<K : Any, V> : Schema<K, V> {
            override val overValues: Iterable<Column.Mutable<K, *>>
        }

        interface CanChangeSchema<K : Any, V> : Schema<K, V>, CanIterateOverValuesAndRemove<Column<K, *>>, CanClear {
            fun <T : V> createColumnIn(changesToValues: Change = Change()): Column<K, T>
        }

        interface Mutable<K : Any, V> : CanChangeValues<K, V>, CanChangeSchema<K, V> {
            override fun <T : V> createColumnIn(changesToValues: Change): Column.Mutable<K, T>
            override fun removeElementFrom(toRemove: Column<K, *>) = removeElementFrom(toRemove as Column.Mutable<K, *>)
            fun removeElementFrom(toRemove: Column.Mutable<K, *>)
        }
    }

    interface Column<K : Any, T> : CanGetAtPlace<K, T>, CanIterateOverValues<T?>, CanContainValue<T> {
        operator fun get(key: K) = getAtPlace(key)

        interface Mutable<K : Any, T> : Column<K, T>, CanSwapAtPlaces<K, T>, CanUnsetAtPlace<K> {
            operator fun set(key: K, value: T) = setAtPlace(key, value)
        }
    }

    fun view(): View<K> = MutableKeySetTable(this)

    interface View<K : Any> : Table<K> {
        override val keySet: KeySet.Mutable<K>
    }

    private class MutableKeySetTable<K : Any>(private val source: Table<K>) : View<K>, Table<K> by source {
        override val keySet: KeySet.Mutable<K> =
            MutableListOrderedKeyHashSet<K>().also { it.addAll(source.keySet.overValues) }
    }
}