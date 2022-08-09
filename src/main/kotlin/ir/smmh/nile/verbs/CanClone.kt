package ir.smmh.nile.verbs

import ir.smmh.nile.Mut
import ir.smmh.nile.RecursivelySpecific

interface CanClone<T> : RecursivelySpecific<T> {
    fun clone(deepIfPossible: Boolean): T

    interface Mutable<T> : CanClone<T>, Mut.Able {
        override fun clone(deepIfPossible: Boolean): T = clone(deepIfPossible, Mut())
        fun clone(deepIfPossible: Boolean, mut: Mut): T
    }
}