package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude

interface CanContainValue<T> : Multitude {
    fun containsValue(toCheck: T): Boolean
    fun doesNotContainValue(toCheck: T) = !containsValue(toCheck)
}