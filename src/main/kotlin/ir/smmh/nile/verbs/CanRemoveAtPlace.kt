package ir.smmh.nile.verbs

import ir.smmh.nile.Multitude


interface CanRemoveAtPlace<P> : CanContainPlace<P>, Multitude.VariableSize {
    fun removeAtPlace(toRemove: P)

    companion object {
        fun <P> removeAtPlace(canRemoveAtPlace: CanRemoveAtPlace<P>, toRemove: P) {
            canRemoveAtPlace.removeAtPlace(toRemove)
        }
    }
}