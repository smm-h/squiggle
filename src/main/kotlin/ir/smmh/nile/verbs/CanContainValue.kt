package ir.smmh.nile.verbs

import ir.smmh.nile.HasSize

interface CanContainValue<T> : HasSize {
    fun containsValue(toCheck: T): Boolean
    fun doesNotContainValue(toCheck: T) = !containsValue(toCheck)
}