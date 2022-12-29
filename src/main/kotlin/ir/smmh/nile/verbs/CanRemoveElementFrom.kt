package ir.smmh.nile.verbs

interface CanRemoveElementFrom<T> : CanChangeSize {
    fun removeElementFrom(toRemove: T)
    fun removeElementsFrom(toRemove: Set<T>) =
        toRemove.forEach { removeElementFrom(it) }
}