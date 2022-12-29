package ir.smmh.nile.verbs


interface CanRemoveAtPlace<P> : CanContainPlace<P>, CanChangeSize {
    fun removeAtPlace(toRemove: P)

    companion object {
        fun <P> removeAtPlace(canRemoveAtPlace: CanRemoveAtPlace<P>, toRemove: P) {
            canRemoveAtPlace.removeAtPlace(toRemove)
        }
    }
}