package ir.smmh.nile.verbs

import ir.smmh.nile.Mut

interface CanSetAtPlace<P, T> : CanContainPlace<P>, CanContainValue<T>, Mut.Able {
    fun setAtPlace(place: P, toSet: T)
}