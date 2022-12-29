package ir.smmh.table

interface View<K : Any> : Table<K> {
    override val keySet: KeySet.Mutable<K>

    private class MutableKeySetTable<K : Any>(private val source: Table<K>) : View<K>, Table<K> by source {
        override val keySet: KeySet.Mutable<K> =
            OrderedHashKeySet<K>().also { it.addAll(source.keySet.overValues) }
    }

    companion object {
        fun <K : Any> Table<K>.view(): View<K> = MutableKeySetTable(this)
    }
}