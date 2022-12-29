package ir.smmh.nile.verbs

import ir.smmh.nile.HasSize

interface CanContainValue<T> : HasSize {
    fun containsValue(toCheck: T): Boolean
    fun doesNotContainValue(toCheck: T) = !containsValue(toCheck)
    fun containsAnyOfValues(toCheck: Iterable<T>): Boolean {
        for (i in toCheck) if (containsValue(i)) return true
        return false
    }
    fun containsNoneOfValues(toCheck: Iterable<T>): Boolean {
        for (i in toCheck) if (!containsValue(i)) return false
        return true
    }
}