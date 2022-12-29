package ir.smmh.nile.verbs

import ir.smmh.nile.HasSize

interface CanGetAtPlace<P, T> : CanContainPlace<P>, HasSize {
    //    operator fun get(place: P) = getAtPlace(place)
    fun getAtPlace(place: P): T = getNullableAtPlace(place)!!
    fun getNullableAtPlace(place: P): T?
}