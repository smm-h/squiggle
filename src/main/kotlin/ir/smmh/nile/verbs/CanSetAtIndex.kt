package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible
import ir.smmh.nile.Mut

interface CanSetAtIndex<T> : Indexible, Mut.Able {
    /**
     * @param index Index
     * @param toSet The object to assign to that index
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun setAtIndex(index: Int, toSet: T)
}