package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible
import ir.smmh.nile.Multitude

interface CanInsertAtIndex<T> : Indexible, Multitude.VariableSize, CanPrependTo<T>, CanAppendTo<T> {
    /**
     * @param index    Index
     * @param toInsert The object to insert at that index
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun insertAtIndex(index: Int, toInsert: T)

    override fun prepend(toPrepend: T) = insertAtIndex(0, toPrepend)

    override fun append(toAppend: T) = insertAtIndex(lastIndex + 1, toAppend)
}