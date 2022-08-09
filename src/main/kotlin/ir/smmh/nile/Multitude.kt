package ir.smmh.nile

interface Multitude {
    val size: Int
    fun isEmpty() = size == 0
    fun isNotEmpty() = !isEmpty()

    fun assertSingleton() {
        assert(size == 1)
    }

    interface VariableSize : Multitude, Mut.Able
}