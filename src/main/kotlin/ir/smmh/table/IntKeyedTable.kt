package ir.smmh.table

import ir.smmh.markup.Markup
import ir.smmh.markup.TableBuilder

class IntKeyedTable(schema: SealableSchema<Int, *>) :
    BaseTable<Int>(schema) {

    private var key = 0

    fun add(toAdd: (Int) -> Unit) =
        add(key++, toAdd)

    override fun clear() {
        key = 0
        super.clear()
    }
}