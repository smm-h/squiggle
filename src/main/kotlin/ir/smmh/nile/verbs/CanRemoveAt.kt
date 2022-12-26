package ir.smmh.nile.verbs

import ir.smmh.nile.CanChangeSize
import ir.smmh.nile.Indexible

interface CanRemoveAt : Indexible, CanChangeSize {
    /**
     * @param toRemove Index to remove
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun removeIndexFrom(toRemove: Int)
}