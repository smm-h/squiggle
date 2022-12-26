package ir.smmh.nile.verbs

import ir.smmh.nile.CanChangeSize

interface CanAddTo<T> : CanChangeSize {
    fun add(toAdd: T)
    fun addAll(toAdd: Iterable<T>) {
        for (i in toAdd) add(i)
    }
}