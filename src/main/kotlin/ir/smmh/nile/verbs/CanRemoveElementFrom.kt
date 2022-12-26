package ir.smmh.nile.verbs

import ir.smmh.nile.CanChangeSize

interface CanRemoveElementFrom<T> : CanChangeSize {
    fun removeElementFrom(toRemove: T)
}