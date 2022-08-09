package ir.smmh.nile

import ir.smmh.nile.verbs.*
import java.util.*


interface Associative<K, V> : CanContainPlace<K>, CanContainValue<V> {
    fun overKeys(): Iterable<K>

    interface Mutable<K, V> : Associative<K, V>, CanSetAtPlace<K, V>, CanRemoveAtPlace<K>, CanClear {
        fun removeAllPlaces()
        override fun clear() = removeAllPlaces()
    }

    interface SingleValue<K, V> : Associative<K, V>, CanGetAtPlace<K, V>, CanClone<SingleValue<K, V>> {
        override fun containsPlace(toCheck: K) = getAtPlace(toCheck) != null
        fun overValues(): Iterable<V>
        interface Mutable<K, V> : Associative.Mutable<K, V>, SingleValue<K, V> {
            override fun clone(deepIfPossible: Boolean): Mutable<K, V>
            fun setAllFrom(map: SingleValue<K, V>) {
                for (key in map.overKeys()) setAtPlace(key, map.getAtPlace(key))
            }

            fun setAllFrom(map: Map<K, V>) {
                for (key in map.keys) setAtPlace(key, map[key]!!)
            }

        }
    }

    interface MultiValue<K, V> : Associative<K, V>, CanGetAtPlace<K, Sequential<V>>, CanClone<MultiValue<K, V>> {
        override fun getAtPlace(place: K): Sequential<V>
        override fun containsPlace(toCheck: K) = count(toCheck) > 0
        fun count(key: K) = getAtPlace(key).size
        fun containingKey(toCheck: V): K?

        interface Mutable<K, V> : Associative.Mutable<K, V>, MultiValue<K, V> {
            override fun clone(deepIfPossible: Boolean): Mutable<K, V>
            fun removeAtPlace(place: K, toRemove: V)
            fun removeAllAtPlace(place: K)
            fun clearAtPlace(place: K) = removeAllAtPlace(place)
            fun addAtPlace(place: K, toAdd: V) = setAtPlace(place, toAdd)
            fun addAllAtPlace(place: K, toSet: Iterable<V>) {
                for (value in toSet) addAtPlace(place, value)
            }

            fun addAllFrom(map: MultiValue<K, V>) {
                for (key in map.overKeys()) addAllAtPlace(key, map.getAtPlace(key))
            }
        }
    }

    private abstract class Impl<K, V> : Associative<K, V> {
        override fun toString() = overKeys().joinToString(", ", "Map: {", "}")
    }

    private open class SingleValueImpl<K, V> protected constructor(protected val map: MutableMap<K, V>) :
        Associative.Impl<K, V>(),
        SingleValue<K, V> {
        constructor() : this(HashMap<K, V>())

        override fun toString() = overKeys().joinToString(", ", "Map: {", "}") { "$it -> ${getAtPlace(it)}" }
        override val size get() = map.size
        override fun isEmpty() = map.isEmpty()
        override fun overKeys(): Iterable<K> = map.keys
        override fun getAtPlace(place: K): V = map[place]!!
        override fun containsPlace(toCheck: K) = map.containsKey(toCheck)
        override fun containsValue(toCheck: V) = map.containsValue(toCheck)
        override fun overValues(): Iterable<V> = map.values
        override fun clone(deepIfPossible: Boolean): SingleValue<K, V> = SingleValueImpl(HashMap(map))
        override fun specificThis(): SingleValue<K, V> = this
    }

    private class SingleValueMutableImpl<K, V> : SingleValueImpl<K, V>, SingleValue.Mutable<K, V>, Mut.Able {

        constructor() : super()
        private constructor(map: MutableMap<K, V>) : super(map)

        override val mut = Mut()

        override fun setAtPlace(place: K, toSet: V) {
            mut.preMutate()
            map[place] = toSet
            mut.mutate()
        }

        override fun removeAtPlace(toRemove: K) {
            mut.preMutate()
            map.remove(toRemove)
            mut.mutate()
        }

        override fun removeAllPlaces() {
            mut.preMutate()
            map.clear()
            mut.mutate()
        }

        override fun clone(deepIfPossible: Boolean): SingleValue.Mutable<K, V> = SingleValueMutableImpl(HashMap(map))
    }

    private open class MultiValueImpl<K, V> protected constructor(protected val map: MutableMap<K, Sequential.Mutable.VariableSize<V>>) :
        Associative.Impl<K, V>(), MultiValue<K, V> {

        constructor() : this(HashMap<K, Sequential.Mutable.VariableSize<V>>())

        override fun toString(): String {
            val joiner = StringJoiner(", ", "Map: {", "}")
            for (key in overKeys()) {
                joiner.add(key.toString() + " -> " + getAtPlace(key))
            }
            return joiner.toString()
        }

        override fun containsValue(toCheck: V): Boolean {
            for (key in map.keys) {
                if (map[key]!!.containsValue(toCheck)) {
                    return true
                }
            }
            return false
        }

        override fun containingKey(toCheck: V): K? {
            for (key in map.keys) {
                if (map[key]!!.containsValue(toCheck)) {
                    return key
                }
            }
            return null
        }

        override fun containsPlace(toCheck: K): Boolean = map.containsKey(toCheck)
        override fun isEmpty(): Boolean = map.isEmpty()
        override fun overKeys(): Iterable<K> = map.keys
        override fun getAtPlace(place: K): Sequential<V> = map[place] ?: Sequential.empty()
        override val size: Int get() = map.size
        override fun clone(deepIfPossible: Boolean): MultiValue<K, V> =
            MultiValueImpl(HashMap<K, Sequential.Mutable.VariableSize<V>>(map))

        override fun specificThis(): MultiValue<K, V> = this
    }

    private class MultiValueMutableImpl<K, V> : MultiValueImpl<K, V>, MultiValue.Mutable<K, V>, Mut.Able {

        override val mut = Mut()

        constructor() : super()
        private constructor(map: MutableMap<K, Sequential.Mutable.VariableSize<V>>) : super(map)

        override fun setAtPlace(place: K, toSet: V) {
            mut.preMutate()
            val s = map.computeIfAbsent(place) { SequentialImpl(ArrayList()) }
            s.append(toSet)
            mut.mutate()
        }

        override fun removeAtPlace(toRemove: K) {
            mut.preMutate()
            map.remove(toRemove)
            mut.mutate()
        }

        override fun removeAtPlace(place: K, toRemove: V) {
            val s = map[place]
            if (s != null) {
                val i = s.findFirst(toRemove)
                if (i != -1) {
                    mut.preMutate()
                    s.removeIndexFrom(i)
                    mut.mutate()
                }
            }
        }

        override fun removeAllAtPlace(place: K) {
            val s = map[place]
            if (s != null) {
                mut.preMutate()
                s.clear()
                mut.mutate()
            }
        }

        override fun removeAllPlaces() {
            mut.preMutate()
            map.clear()
            mut.mutate()
        }

        override fun clone(deepIfPossible: Boolean): MultiValue.Mutable<K, V> = MultiValueMutableImpl(HashMap(map))
    }
}