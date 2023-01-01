package ir.smmh.nile

interface BiDirectionalMap<K, V> : Map<K, V> {
    val reverse: Map<V, K>

    private class Impl<K, V>(private val map: Map<K, V>) : BiDirectionalMap<K, V>, Map<K, V> by map {
        override val reverse: Map<V, K> by lazy { HashMap<V, K>(map.size).apply { for ((k, v) in map) put(v, k) } }
    }

    companion object {
        fun <K, V> of(map: Map<K, V>): BiDirectionalMap<K, V> = Impl(map)
        fun <T> indexMapOf(iterable: Iterable<T>): BiDirectionalMap<T, Int> {
            var i = 0
            return of(iterable.associateWith { i++ })
        }
    }
}