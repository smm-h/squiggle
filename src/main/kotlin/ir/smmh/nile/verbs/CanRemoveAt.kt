package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible
import ir.smmh.nile.Multitude

interface CanRemoveAt : Indexible, Multitude.VariableSize {
    /**
     * @param toRemove Index to remove
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun removeIndexFrom(toRemove: Int)
}