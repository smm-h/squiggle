package ir.smmh.nile.verbs

interface CanPrependTo<T> {
    fun prepend(toPrepend: T)
    fun prependAll(toPrepend: Iterable<T>) {
        for (i in toPrepend) prepend(i)
    }
}