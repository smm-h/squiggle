package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude

interface CanContainPlace<P> : Multitude {
    fun containsPlace(toCheck: P): Boolean
    fun doesNotContainPlace(toCheck: P) = !containsPlace(toCheck)
}