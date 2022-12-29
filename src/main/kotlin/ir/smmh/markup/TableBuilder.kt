package ir.smmh.markup

import ir.smmh.table.Column
import ir.smmh.table.NamedSchema
import ir.smmh.table.Schema
import ir.smmh.table.Table

class TableBuilder<K : Any, V>(val convertKey: (K) -> Int = Any::hashCode) {

    interface CanCreateTableBuilder<K : Any, V> : Schema<K, V> {
        fun createTableBuilder(table: Table<K>): TableBuilder<K, V>
    }

    // table settings
    var showIndexColumn: Boolean = false
    val rowHyperdata:
            MutableMap<Int, String> = HashMap()
    var rowHyperdataIfNull: String = ""

    // column settings
    val titleFragment:
            MutableMap<Column<K, *>, Markup.Fragment> = HashMap()
    val titleHyperdata:
            MutableMap<Column<K, *>, String> = HashMap()
    val cellFragmentIfNull:
            MutableMap<Column<K, *>, Markup.Fragment> = HashMap()
    val cellHyperdataIfNull:
            MutableMap<Column<K, *>, String> = HashMap()
    val cellDirection:
            MutableMap<Column<K, *>, TextDirection> = HashMap()
    val titleDirection:
            MutableMap<Column<K, *>, TextDirection?> = HashMap()

    private val fragmentMakers:
            MutableMap<Column<K, *>, Any> = HashMap()
    private val hyperdataMakers:
            MutableMap<Column<K, *>, Any> = HashMap()

    fun <T> makeFragment(column: Column<K, T>, function: (T) -> Markup.Fragment) {
        fragmentMakers[column] = function
    }

    fun <T> makeHyperdata(column: Column<K, T>, function: (T) -> String?) {
        hyperdataMakers[column] = function
    }

    fun build(table: Table<K>) =
        Markup.Table(showIndexColumn, rowHyperdata, rowHyperdataIfNull).apply {

            // add the pre-ordered row keys
            table.keySet.overValues.forEach { addRow(convertKey(it)) }

            // add the index column if it necessary
            if (showIndexColumn) {
                var k = 0
                Column(
                    Markup.Tools.atom("#"),
                    cellDirection = TextDirection.RIGHT_TO_LEFT,
                    cellFragments = table.keySet.overValues
                        .associateBy(convertKey) { Markup.Tools.atom((k++).toString()) },
                )
            }

            // add the columns
            @Suppress("UNCHECKED_CAST")
            table.schema.overValues.forEach { c ->
                val fragmentMaker = (fragmentMakers[c] ?: ::defaultFragmentMaker) as (Any) -> Markup.Fragment
                val hyperdataMaker = (hyperdataMakers[c] ?: ::defaultHyperdataMaker) as (Any) -> String
                val th = titleHyperdata[c]
                val tf = titleFragment[c] ?: Markup.Tools.atom(
                    (table.schema as NamedSchema<K, V>)
                        .findNameOfColumn(c as Column<K, V>)!!
                )
                Column(
                    if (th == null) tf else Markup.Tools.span(tf, th),
                    cellFragments = table.keySet.overValues.filter { c[it] != null }
                        .associateBy(convertKey) { fragmentMaker(c[it]!!) },
                    cellHyperdata = table.keySet.overValues.filter { c[it] != null }
                        .associateBy(convertKey) { hyperdataMaker(c[it]!!) },
                )
            }
        }

    companion object {
        private fun defaultFragmentMaker(it: Any): Markup.Fragment =
            if (it is Markup.Fragment) it else Markup.Tools.atom(it.toString())

        @Suppress("UNUSED_PARAMETER")
        private fun defaultHyperdataMaker(it: Any): String? = null

        fun <K : Any, V> Schema<K, V>.createTableBuilder(table: Table<K>): TableBuilder<K, V> =
            if (this is CanCreateTableBuilder<K, V>) createTableBuilder(table) else TableBuilder<K, V>()

        fun <K : Any> Table<K>.toMarkupTable(): Markup.Table =
            schema.createTableBuilder(this).build(this)
    }
}