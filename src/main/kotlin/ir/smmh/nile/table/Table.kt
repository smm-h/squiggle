package ir.smmh.nile.table

import ir.smmh.nile.Change
import java.io.File

class Table(
    val changesToValues: Change = Change(),
    override val changesToSize: Change = Change(),
) : TableView(emptyList(), emptyList()), Tabular.Mutable, Tabular.MutableSchema, Tabular.View {
    override val core = this

    override val changesToSchema: Change = Change()

    override fun <T> addColumn(name: String): Tabular.Column.Mutable<T> {
        val column = Column<T>(name)
        changesToSchema.beforeChange()
        columns.add(column)
        changesToSchema.afterChange()
        return column
    }

    fun removeColumn(column: Tabular.Column.Mutable<*>) {
        if (column in columns) {
            changesToSchema.beforeChange()
            columns.remove(column)
            changesToSchema.afterChange()
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
    ) : Tabular.Column.Mutable<T> {
        override val changesToValues: Change by this@Table::changesToValues
        override val changesToSize: Change by this@Table::changesToSize
        private val data: MutableMap<Int, T> = HashMap()
        override val size: Int get() = data.size
        override fun iterator(): Iterator<T?> = data.values.iterator()
        override fun getAtIndex(index: Int): T? = data[index]
        override fun containsValue(toCheck: T): Boolean = data.containsValue(toCheck)

        override fun setAtIndex(index: Int, toSet: T?) {
            changesToValues.beforeChange()
            if (toSet == null) data.remove(index) else data[index] = toSet
            changesToValues.afterChange()
        }

        override fun removeIndexFrom(toRemove: Int) {
            if (toRemove in data) {
                changesToValues.beforeChange()
                data.remove(toRemove)
                changesToValues.afterChange()
            }
        }

        override fun clear() {
            if (data.isNotEmpty()) {
                changesToValues.beforeChange()
                data.clear()
                changesToValues.afterChange()
            }
        }
    }

    private var nextKey = 0

    @Suppress("UNCHECKED_CAST")
    override fun overColumns() =
        super.overColumns() as Iterable<Tabular.Column.Mutable<*>>

    override fun findNullableColumnByName(name: String) =
        super.findNullableColumnByName(name) as Tabular.Column.Mutable<*>?

    override fun removeIndexFrom(toRemove: Int) =
        removeElementFrom(rows[toRemove])

    override fun removeElementFrom(toRemove: Int) {
        changesToSize.beforeChange()
        rows.remove(toRemove)
        overColumns().forEach { it.removeIndexFrom(toRemove) }
        changesToSize.afterChange()
    }

    override fun clear() {
        changesToSize.beforeChange()
        nextKey = 0
        rows.clear()
        overColumns().forEach { it.clear() }
        changesToSize.afterChange()
    }

    override fun add(toAdd: (Int) -> Unit) {
        changesToSize.beforeChange()
        val k = nextKey++
        this.rows.add(k)
        toAdd(k)
        changesToSize.afterChange()
    }

    companion object {
        fun fromCsv(file: File, change: Change = Change()) =
            fromCsv(file.readText(), change)

        fun fromCsv(string: String, change: Change = Change()) =
            fromSv(string, ",", "\n", change)

        fun fromTsv(file: File, change: Change = Change()) =
            fromTsv(file.readText(), change)

        fun fromTsv(string: String, change: Change = Change()) =
            fromSv(string, "\t", "\n", change)

        fun fromSv(file: File, seperator: String, linebreak: String, change: Change = Change()) =
            fromSv(file.readText(), seperator, linebreak, change)

        fun fromSv(string: String, seperator: String, linebreak: String, change: Change = Change()): Table {
            val table = Table(change)
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

