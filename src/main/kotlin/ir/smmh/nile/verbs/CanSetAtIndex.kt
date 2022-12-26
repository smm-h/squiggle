package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible

interface CanSetAtIndex<T> : Indexible, CanChangeValues {
    /**
     * @param index Index
     * @param toSet The object to assign to that index
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun setAtIndex(index: Int, toSet: T)
}