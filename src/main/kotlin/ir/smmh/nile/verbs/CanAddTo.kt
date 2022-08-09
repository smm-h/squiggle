package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude

interface CanAddTo<T> : Multitude.VariableSize {
    fun add(toAdd: T)
    fun addAll(toAdd: Iterable<T>) {
        for (i in toAdd) add(i)
    }
}