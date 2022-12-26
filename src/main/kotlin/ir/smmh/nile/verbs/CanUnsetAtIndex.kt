package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible

interface CanUnsetAtIndex : Indexible, CanChangeValues {
    /**
     * @param index Index
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun unsetAtIndex(index: Int)

    fun unsetAll()
}