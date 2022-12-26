package ir.smmh.nile.verbs

interface CanSetAtPlace<P, T> : CanContainPlace<P>, CanContainValue<T>, CanChangeValues {
    fun setAtPlace(place: P, toSet: T)
}