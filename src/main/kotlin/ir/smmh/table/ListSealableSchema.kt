package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanUnsetAtPlace

class ListSealableSchema<K : Any, V>(
    override val changesToSize: Change = Change(),
) : SealableSchema<K, V> {
    private val list: MutableList<Table.Column.Mutable<K, *>> = ArrayList()

    override val size: Int by list::size

    val columns: List<Table.Column.Mutable<K, *>> get() = list

    override var sealed: Boolean = false
        private set

    override fun seal() {
        if (sealed) throw Exception("SealableListSchema already sealed") else
            sealed = true
    }

    override val overValues: Iterable<Table.Column.Mutable<K, *>> get() = list
    override val overValuesMutably: MutableIterable<Table.Column<K, *>> get() = list

    override fun <T : V> createColumnIn(changesToValues: Change): Table.Column.Mutable<K, T> =
        HashColumn<K, T>(changesToValues).also {
            if (sealed) throw Exception("cannot createColumnIn sealed SealableListSchema") else {
                changesToSize.beforeChange()
                list.add(it)
                changesToSize.afterChange()
            }
        }

    override fun removeElementFrom(toRemove: Table.Column.Mutable<K, *>) {
        if (sealed) throw Exception("cannot removeElementFrom sealed SealableListSchema") else {
            val index = list.indexOf(toRemove)
            if (index == -1) throw IllegalArgumentException("column not in table") else {
                toRemove.unsetAll()
                changesToSize.beforeChange()
                list.removeAt(index)
                changesToSize.afterChange()
            }
        }
    }

    override fun clear() {
        if (sealed) throw Exception("cannot clear sealed SealableListSchema") else {
            list.forEach(CanUnsetAtPlace<K>::unsetAll)
            changesToSize.beforeChange()
            list.clear()
            changesToSize.afterChange()
        }
    }
}