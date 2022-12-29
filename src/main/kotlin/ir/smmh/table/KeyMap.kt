package ir.smmh.table

object KeyMap {

    fun <K : Any, T, N : T & Any> of(table: Table<K>, keyColumn: Column<K, T>): Table<N> =
        of(table, keyColumn, { @Suppress("UNCHECKED_CAST") (it as N) })

    fun <K : Any, T, N : T & Any> of(table: Table<K>, keyColumn: Column<K, T>, cast: (T & Any) -> N): Table<N> {

        // create an empty set to add the new keys into
        val newKeys: MutableSet<N> = HashSet()

        // iterate over this column, and add non-null values as new keys
        keyColumn.overValues.forEach { if (it != null) newKeys.add(cast(it)) }

        // make sure there are as many new keys as old ones
        require(table.keySet.size == newKeys.size)

        // map new keys to old ones
        val keyMap: Map<N, K> = table.keySet.overValues.associateBy { cast(keyColumn.getAtPlace(it)!!) }

        // create a list of new columns, made by mapping new keys to old keys in old columns
        val columns = table.schema.overValues.map { KeyMappedColumn(it, keyMap::getValue) }

        return KeyMappedTable<N>(IterableSchema(columns), HashKeySet(newKeys))
    }

    private class KeyMappedColumn<K : Any, N : Any, T>(
        private val source: Column<K, T>,
        private val keyMap: (N) -> K
    ) : Column<N, T> {
        override val size: Int by source::size
        override val overValues: Iterable<T?> = source.overValues
        override fun containsValue(toCheck: T) = source.containsValue(toCheck)
        override fun containsPlace(toCheck: N) = source.containsPlace(keyMap(toCheck))
        override fun getNullableAtPlace(place: N): T? = source.getNullableAtPlace(keyMap(place))
    }

    private class IterableSchema<K : Any>(override val overValues: Iterable<Column<K, *>>) : Schema<K, Any?>

    private class KeyMappedTable<N : Any>(
        override val schema: Schema<N, *>,
        override val keySet: KeySet<N>,
    ) : Table<N>
}