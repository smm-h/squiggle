package ir.smmh.table

import ir.smmh.nile.Change
import java.io.File

object StringTable {
    fun csv(file: File, change: Change = Change()) =
        csv(file.readText(), change)

    fun csv(string: String, change: Change = Change()) =
        of(string, ",", "\n", change)

    fun tsv(file: File, change: Change = Change()) =
        tsv(file.readText(), change)

    fun tsv(string: String, change: Change = Change()) =
        of(string, "\t", "\n", change)

    fun of(file: File, cellSeperator: String, rowSeperator: String, change: Change = Change()) =
        of(file.readText(), cellSeperator, rowSeperator, change)

    fun of(string: String, cellSeperator: String, rowSeperator: String, change: Change = Change()): Table<Int, String> {
        val lines = string.split(rowSeperator).iterator()
        val schema = NamedSchema<Int, String>(lines.next().split(cellSeperator))
        return IntKeyedTable(schema).also { table ->
            lines.forEach { line ->
                table.add { key ->
                    line.split(cellSeperator).forEachIndexed { index, value ->
                        schema.getAtIndex(index)[key] = value
                    }
                }
            }
        }
    }
}