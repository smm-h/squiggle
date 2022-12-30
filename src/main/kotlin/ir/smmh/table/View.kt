package ir.smmh.table

interface View<K : Any, V> : Table<K, V> {
    override val keySet: OrderedKeySet.Mutable<K>

    private class MutableKeySetTable<K : Any, V>(private val source: Table<K, V>) : View<K, V>, Table<K, V> by source {
        override val keySet: OrderedKeySet.Mutable<K> =
            OrderedHashKeySet<K>().also { it.addAll(source.keySet.overValues) }
    }

    companion object {
        fun <K : Any, V> Table<K, V>.view(): View<K, V> = MutableKeySetTable(this)
    }
}