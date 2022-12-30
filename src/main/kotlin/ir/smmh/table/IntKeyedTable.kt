package ir.smmh.table

class IntKeyedTable<V>(schema: SealableSchema<Int, V>) :
    BaseTable<Int, V>(schema) {

    private var key = 0

    fun add(setup: (Int) -> Unit) =
        add(key++, setup)

    override fun clear() {
        key = 0
        super.clear()
    }
}