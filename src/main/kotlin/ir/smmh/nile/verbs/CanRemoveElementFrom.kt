package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude

interface CanRemoveElementFrom<T> : Multitude.VariableSize {
    fun removeElementFrom(toRemove: T)
}