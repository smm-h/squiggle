package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.util.FunctionalUtil.not


open class BaseTable<K : Any>(
    override val schema: Table.Schema.CanChangeValues<K, *>,
    override val changesToSize: Change = Change(),
) : Table.CanChangeValues<K> {

    override val keySet = object : KeySet.CanChangeSize<K> {

        override val changesToSize: Change get() = this@BaseTable.changesToSize

        private val set: MutableSet<K> = HashSet()

        override val size by set::size

        override fun containsValue(toCheck: K) = set.contains(toCheck)

        override val overValues: Iterable<K> = set
        override val overValuesMutably: MutableIterable<K> = set

        override fun add(toAdd: K) {
            if (set.contains(toAdd)) throw IllegalArgumentException("duplicate key: $toAdd") else {
                changesToSize.beforeChange()
                set.add(toAdd)
                changesToSize.afterChange()
            }
        }

        override fun removeElementFrom(toRemove: K) {
            if (set.contains(toRemove)) {
                changesToSize.beforeChange()
                set.remove(toRemove)
                schema.overValues.forEach { it.unsetAtPlace(toRemove) }
                changesToSize.afterChange()
            }
        }

        private fun containsAnyOf(iterable: Iterable<K>): Boolean {
            for (i in iterable) if (set.contains(i)) return true
            return false
        }

        override fun removeElementsFrom(toRemove: Set<K>) {
            if (this.isNotEmpty() && toRemove.isNotEmpty() && this.containsAnyOf(toRemove)) {
                changesToSize.beforeChange()
                set.removeAll(toRemove)
                schema.overValues.forEach { it.unsetAtPlaces(toRemove) }
                changesToSize.afterChange()
            }
        }

        override fun clear() {
            if (isNotEmpty()) {
                changesToSize.beforeChange()
                set.clear()
                schema.overValues.forEach { it.unsetAll() }
                changesToSize.afterChange()
            }
        }

        override fun filterBy(predicate: (K) -> Boolean) {
            if (isNotEmpty()) {
                removeElementsFrom(set.filter(!predicate).toSet())
            }
        }
    }
}

