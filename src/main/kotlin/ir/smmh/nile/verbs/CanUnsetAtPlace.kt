package ir.smmh.nile.verbs

interface CanUnsetAtPlace<P> : CanContainPlace<P>, CanChangeValues {
    fun unsetAtPlace(place: P)
    fun unsetAtPlaces(places: Set<P>)
    fun unsetAll()
}