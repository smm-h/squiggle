package ir.smmh.table

import ir.smmh.nile.Change
import ir.smmh.util.FunctionalUtil.not
import kotlin.random.Random

class OrderedHashKeySet<K : Any> private constructor(
    override val changesToSize: Change = Change(),
    override val changesToOrder: Change = Change(),
    private val set: MutableSet<K>,
    private val list: MutableList<K>,
) : OrderedKeySet.Mutable<K> {

    constructor(changesToSize: Change = Change(), changesToOrder: Change = Change()) :
            this(changesToSize, changesToOrder, HashSet(), ArrayList())

    override val size by set::size

    override fun getAtIndex(index: Int) = list[index]
    override fun containsValue(toCheck: K) = set.contains(toCheck)

    override val overValues: Iterable<K> = list
    override val overValuesInReverse: Iterable<K> = list.asReversed()
    override val overValuesMutably: MutableIterable<K> = list

    override fun getMutableCopy(changesToSize: Change, changesToOrder: Change): OrderedKeySet.Mutable<K> =
        OrderedHashKeySet(changesToSize, changesToOrder, HashSet(set), ArrayList(list))

    override fun add(toAdd: K) {
        if (set.contains(toAdd)) throw IllegalArgumentException("duplicate key: $toAdd") else {
            changesToSize.beforeChange()
            set.add(toAdd)
            list.add(toAdd)
            changesToSize.afterChange()
        }
    }

    override fun removeElementFrom(toRemove: K) {
        if (set.contains(toRemove)) {
            changesToSize.beforeChange()
            set.remove(toRemove)
            list.remove(toRemove)
            changesToSize.afterChange()
        }
    }

    override fun clear() {
        if (isNotEmpty()) {
            changesToSize.beforeChange()
            set.clear()
            list.clear()
            changesToSize.afterChange()
        }
    }

    override fun reverse() {
        if (isNotEmpty()) {
            changesToOrder.beforeChange()
            list.reverse()
            changesToOrder.afterChange()
        }
    }

    override fun shuffle(random: Random) {
        if (isNotEmpty()) {
            changesToOrder.beforeChange()
            list.shuffle(random)
            changesToOrder.afterChange()
        }
    }

    override fun sortBy(ascending: Boolean, sortingFunction: (K) -> Int) {
        if (isNotEmpty()) {
            changesToOrder.beforeChange()
            if (ascending) list.sortBy(sortingFunction) else list.sortByDescending(sortingFunction)
            changesToOrder.afterChange()
        }
    }

    override fun filterBy(predicate: (K) -> Boolean) {
        if (isNotEmpty()) {
            changesToSize.beforeChange()
            if (list.removeIf(!predicate))
                changesToSize.afterChange()
        }
    }
}