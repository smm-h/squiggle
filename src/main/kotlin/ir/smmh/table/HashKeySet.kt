package ir.smmh.table

class HashKeySet<K : Any>(keys: Iterable<K>) : KeySet<K> {
    private val set = HashSet<K>().also { it.addAll(keys) }
    override val size: Int by set::size
    override val overValues: Iterable<K> = set
    override fun containsValue(toCheck: K) = set.contains(toCheck)
}