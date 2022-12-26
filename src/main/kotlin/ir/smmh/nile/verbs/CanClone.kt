package ir.smmh.nile.verbs

import ir.smmh.nile.RecursivelySpecific

interface CanClone<T> : RecursivelySpecific<T> {
    fun clone(deepIfPossible: Boolean): T
}