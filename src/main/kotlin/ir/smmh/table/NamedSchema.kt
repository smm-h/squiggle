package ir.smmh.table

import ir.smmh.nile.BiDirectionalMap
import ir.smmh.nile.verbs.CanGetAtIndex

class NamedSchema<K : Any, V>(val names: List<String>) :
    SealableSchema.Delegated<K, V>(), CanGetAtIndex<Column.Mutable<K, V>> {

    private val columns = names.map { createColumnIn<V>() }

    override fun getAtIndex(index: Int): Column.Mutable<K, V> = columns[index]

    private val map: BiDirectionalMap<String, Column.Mutable<K, V>> =
        BiDirectionalMap.of((0 until size).associate { names[it] to columns[it] })

    fun findColumnByName(name: String) =
        map[name]

    fun findNameOfColumn(column: Column<K, V>) =
        map.reverse[column]

    init {
        seal()
    }
}