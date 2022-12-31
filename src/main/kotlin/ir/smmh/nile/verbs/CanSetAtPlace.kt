package ir.smmh.nile.verbs

interface CanSetAtPlace<P, T> : CanContainPlace<P>, CanChangeValues {
    fun setAtPlace(place: P, toSet: T)
}