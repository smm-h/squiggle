package ir.smmh.nile.table

import ir.smmh.nile.Mut
import java.io.File

class Table(override val mut: Mut = Mut()) :
    TableView(emptyList(), emptyList()), Tabular.Mutable, Tabular.MutableSchema, Tabular.View, Mut.Able {
    override val core = this

    override val schemaMut: Mut = Mut()

    override fun <T> addColumn(name: String): Tabular.Column.Mutable<T> {
        val column = Column<T>(name)
        schemaMut.preMutate()
        columns.add(column)
        schemaMut.mutate()
        return column
    }

    fun removeColumn(column: Tabular.Column.Mutable<*>) {
        if (column in columns) {
            schemaMut.preMutate()
            columns.remove(column)
            schemaMut.mutate()
            column.clear()
        } else {
            throw IllegalArgumentException("column not in table")
        }
    }

    fun removeColumn(name: String) {
        findNullableColumnByName(name)?.let { removeColumn(it) }
            ?: throw NoSuchElementException("no such column: $name")
    }

    private inner class Column<T>(
        override val name: String,
        override val mut: Mut = this.mut
    ) : Tabular.Column.Mutable<T>, Mut.Able {
        private val data: MutableMap<Int, T> = HashMap()
        override val size: Int get() = data.size
        override fun iterator(): Iterator<T?> = data.values.iterator()
        override fun getAtIndex(index: Int): T? = data[index]
        override fun containsValue(toCheck: T): Boolean = data.containsValue(toCheck)
        override fun setAtIndex(index: Int, toSet: T?) {
            if (toSet == null) data.remove(index) else data[index] = toSet
        }

        override fun removeIndexFrom(toRemove: Int) {
            if (toRemove in data) {
                mut.preMutate()
                data.remove(toRemove)
                mut.mutate()
            }
        }

        override fun clear() {
            if (data.isNotEmpty()) {
                mut.preMutate()
                data.clear()
                mut.mutate()
            }
        }
    }

    private var nextKey = 0

    @Suppress("UNCHECKED_CAST")
    override fun overColumns() =
        super.overColumns() as Iterable<Tabular.Column.Mutable<*>>

    override fun findNullableColumnByName(name: String) =
        super.findNullableColumnByName(name) as Tabular.Column.Mutable<*>?

    override fun removeIndexFrom(toRemove: Int) {
        mut.preMutate()
        removeElementFrom(rows[toRemove])
        mut.mutate()
    }

    override fun removeElementFrom(toRemove: Int) {
        mut.preMutate()
        rows.remove(toRemove)
        overColumns().forEach { it.removeIndexFrom(toRemove) }
        mut.mutate()
    }

    override fun clear() {
        mut.preMutate()
        nextKey = 0
        rows.clear()
        overColumns().forEach() { it.clear() }
        mut.mutate()
    }

    override fun add(toAdd: (Int) -> Unit) {
        mut.preMutate()
        val k = nextKey++
        this.rows.add(k)
        toAdd(k)
        mut.mutate()
    }

    companion object {
        fun fromCsv(file: File, mut: Mut = Mut()) =
            fromCsv(file.readText(), mut)

        fun fromCsv(string: String, mut: Mut = Mut()) =
            fromSv(string, ",", "\n", mut)

        fun fromTsv(file: File, mut: Mut = Mut()) =
            fromTsv(file.readText(), mut)

        fun fromTsv(string: String, mut: Mut = Mut()) =
            fromSv(string, "\t", "\n", mut)

        fun fromSv(file: File, seperator: String, linebreak: String, mut: Mut = Mut()) =
            fromSv(file.readText(), seperator, linebreak, mut)

        fun fromSv(string: String, seperator: String, linebreak: String, mut: Mut = Mut()): Table {
            val table = Table(mut)
            val columns: MutableList<Tabular.Column.Mutable<String>> = ArrayList()
            var afterFirstLine = false
            string.split(linebreak).forEach {
                val line = it.trim().split(seperator)
                if (afterFirstLine) {
                    table.add { key ->
                        line.forEachIndexed { index, value ->
                            columns[index][key] = value
                        }
                    }
                } else {
                    afterFirstLine = true
                    line.forEach {
                        columns.add(table.addColumn<String>(it))
                    }
                }
            }
            return table
        }
    }
}

