package ir.smmh.table

import ir.smmh.nile.BiDirectionalMap

class NamedSchema<K : Any, V>(val names: List<String>) : SealableSchema.Delegated<K, V>() {

    val columns = names.map { createColumnIn<V>() }

    private val map: BiDirectionalMap<String, Column<K, V>> = BiDirectionalMap(names, columns)

    fun findColumnByName(name: String) =
        map.forward[name]

    fun findNameOfColumn(column: Column<K, V>) =
        map.backward[column]

    init {
        seal()
    }
}