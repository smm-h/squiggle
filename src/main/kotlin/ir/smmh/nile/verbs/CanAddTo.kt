package ir.smmh.nile.verbs

interface CanAddTo<T> : CanChangeSize {
    fun add(toAdd: T)
    fun addAll(toAdd: Iterable<T>) {
        for (i in toAdd) add(i)
    }
}