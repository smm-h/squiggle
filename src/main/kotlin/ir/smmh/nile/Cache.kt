package ir.smmh.nile

/**
 * A `Cache` is a deterministic function K -> V, where K and V are immutable
 * types. Since this function is deterministic, its results are stored, or
 * "cached", and merely looked-up after the first time they are invoked.
 */

class Cache<K : Any, V : Any>(val preprocessKey: (K) -> K = { it }, val create: (K) -> V) : (K) -> V {
    private val map: MutableMap<K, V> = HashMap()

    override fun invoke(k: K): V {
        val key = preprocessKey(k)
        return map[key] ?: try {
            create(key).also { map[key] = it }
        } catch (throwable: Throwable) {
            throw CachingException(throwable)
        }
    }

    fun invalidate(key: K) {
        map.remove(preprocessKey(key))
    }

    fun invalidateAll() {
        map.clear()
    }

    class CachingException(throwable: Throwable) : Exception(throwable)
}