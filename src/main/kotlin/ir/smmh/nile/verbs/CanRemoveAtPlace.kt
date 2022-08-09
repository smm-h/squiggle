package ir.smmh.nile.verbs

import ir.smmh.nile.Mut


interface CanRemoveAtPlace<P> : CanContainPlace<P>, Mut.Able {
    fun removeAtPlace(toRemove: P)

    companion object {
        fun <P> removeAtPlace(canRemoveAtPlace: CanRemoveAtPlace<P>, toRemove: P) {
            canRemoveAtPlace.removeAtPlace(toRemove)
        }
    }
}