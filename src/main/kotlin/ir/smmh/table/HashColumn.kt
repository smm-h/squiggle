package ir.smmh.table

import ir.smmh.nile.Change

class HashColumn<K : Any, T>(
    override val changesToValues: Change = Change(),
) : Column.Mutable<K, T> {
    private val map: MutableMap<K, T> = HashMap()

    override val size by map::size
    override val overValues: Iterable<T?> by map::values

    override fun containsPlace(toCheck: K) = map.containsKey(toCheck)
    override fun containsValue(toCheck: T) = map.containsValue(toCheck)

    override fun getNullableAtPlace(place: K): T = map.getValue(place)
    override fun setAtPlace(place: K, toSet: T) {
        changesToValues.beforeChange()
        map[place] = toSet
        changesToValues.afterChange()
    }

    override fun unsetAtPlace(place: K) {
        if (place in map) {
            changesToValues.beforeChange()
            map.remove(place)
            changesToValues.afterChange()
        }
    }

    override fun unsetAtPlaces(places: Set<K>) {
        TODO("Not yet implemented")
    }

    override fun unsetAll() {
        if (map.isNotEmpty()) {
            changesToValues.beforeChange()
            map.clear()
            changesToValues.afterChange()
        }
    }
}