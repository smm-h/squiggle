package ir.smmh.nile.verbs

import ir.smmh.nile.CanChangeSize


interface CanRemoveAtPlace<P> : CanContainPlace<P>, CanChangeSize {
    fun removeAtPlace(toRemove: P)

    companion object {
        fun <P> removeAtPlace(canRemoveAtPlace: CanRemoveAtPlace<P>, toRemove: P) {
            canRemoveAtPlace.removeAtPlace(toRemove)
        }
    }
}