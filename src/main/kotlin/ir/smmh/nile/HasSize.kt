package ir.smmh.nile

interface HasSize {
    val size: Int

    fun isEmpty() = size == 0
    fun isNotEmpty() = !isEmpty()
    fun assertSingleton() = assert(size == 1)
}