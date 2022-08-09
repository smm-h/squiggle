package ir.smmh.nile

/**
 * A `Cache` is a deterministic function K -> V, where K and V are immutable
 * types. Since this function is deterministic, its results are stored, or
 * "cached", and merely looked-up after the first time they are invoked.
 */
class Cache<K, V> constructor(
    private val storage: MutableMap<K, V> = HashMap(),
    private val preprocessKey: (K) -> K = { it },
    private val create: (K) -> V,
) : (K) -> V {

    @Throws(Exception::class)
    override fun invoke(k: K): V {
        val key = preprocessKey(k)
        val storedValue = storage[key]
        return if (storedValue == null) {
            val newValue: V
            try {
                newValue = create(key)
            } catch (throwable: Throwable) {
                throw Exception(throwable)
            }
            storage[key] = newValue
            newValue
        } else storedValue
    }

    class Nullable<K, V>(
        private val storage: MutableMap<K, V?> = HashMap(),
        private val preprocessKey: (K) -> K = { it },
        private val create: (K) -> V,
    ) : (K) -> V? {
        @Throws(Exception::class)
        override fun invoke(k: K): V? {
            val key = preprocessKey(k)
            val storedValue = storage[key]
            return if (storedValue == null) {
                val newValue: V? = try {
                    create(key)
                } catch (throwable: Throwable) {
                    null
                }
                storage[key] = newValue
                newValue
            } else storedValue
        }
    }

    class Exception(throwable: Throwable) : kotlin.Exception("caching failed", throwable)
}