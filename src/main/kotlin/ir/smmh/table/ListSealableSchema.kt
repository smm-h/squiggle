package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.nile.verbs.CanUnsetAtPlace

class ListSealableSchema<K : Any, V>(
    override val changesToSize: Change = Change(),
) : SealableSchema<K, V> {
    private val list: MutableList<Column.Mutable<K, out V>> = ArrayList()

    override val size: Int by list::size

    val columns: List<Column.Mutable<K, out V>> get() = list

    override var sealed: Boolean = false
        private set

    override fun seal() {
        if (sealed) throw Exception("SealableListSchema already sealed") else
            sealed = true
    }

    override val overValues: Iterable<Column.Mutable<K, out V>> get() = list
    override val overValuesMutably: MutableIterable<Column.Mutable<K, out V>> get() = list

    override fun <T : V> createColumnIn(changesToValues: Change): Column.Mutable<K, T> =
        HashColumn<K, T>(changesToValues).also {
            if (sealed) throw Exception("cannot createColumnIn sealed SealableListSchema") else {
                changesToSize.beforeChange()
                list.add(it)
                changesToSize.afterChange()
            }
        }

    override fun removeElementFrom(toRemove: Column<K, out V>) {
        if (sealed) throw Exception("cannot removeElementFrom sealed SealableListSchema") else {
            val index = list.indexOf(toRemove)
            if (index == -1) throw IllegalArgumentException("column not in table") else {
                if (toRemove is Column.Mutable<K, out V>) {
                    toRemove.unsetAll()
                }
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