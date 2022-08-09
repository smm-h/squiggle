package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude

interface CanGetAtPlace<P, T> : CanContainPlace<P>, Multitude {
    //    operator fun get(place: P) = getAtPlace(place)
    fun getAtPlace(place: P): T
    fun getNullableAtPlace(place: P): T? = if (containsPlace(place)) getAtPlace(place) else null
}