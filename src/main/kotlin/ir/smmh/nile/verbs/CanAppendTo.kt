package ir.smmh.nile.verbs

interface CanAppendTo<T> : CanAddTo<T> {
    fun append(toAppend: T)
    override fun add(toAdd: T) = append(toAdd)
    fun appendAll(toAppend: Iterable<T>) {
        for (i in toAppend) append(i)
    }
}