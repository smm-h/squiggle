package ir.smmh.nile.verbs

import ir.smmh.nile.HasSize

interface CanContainPlace<P> : HasSize {
    fun containsPlace(toCheck: P): Boolean
    fun doesNotContainPlace(toCheck: P) = !containsPlace(toCheck)
}