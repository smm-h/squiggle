package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible

interface CanGetAtIndex<T> : Indexible {

    /**
     * @param index Index
     * @return Object at that index
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun getAtIndex(index: Int): T

    fun getAtFirstIndex(): T =
        getAtIndex(0)

    fun getAtLastIndex(): T =
        getAtIndex(lastIndex)

    val singleton: T
        get() {
            assertSingleton()
            return getAtFirstIndex()
        }
}