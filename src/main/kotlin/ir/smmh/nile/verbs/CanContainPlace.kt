package ir.smmh.nile.verbs

import ir.smmh.nile.HasSize

interface CanContainPlace<P> : HasSize {
    fun containsPlace(toCheck: P): Boolean
    fun doesNotContainPlace(toCheck: P) = !containsPlace(toCheck)
    fun containsAnyOfPlaces(toCheck: Iterable<P>): Boolean {
        for (i in toCheck) if (containsPlace(i)) return true
        return false
    }

    fun containsNoneOfPlaces(toCheck: Iterable<P>): Boolean {
        for (i in toCheck) if (!containsPlace(i)) return false
        return true
    }
}