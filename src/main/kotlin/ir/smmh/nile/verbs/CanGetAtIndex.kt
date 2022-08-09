package ir.smmh.nile.verbs

import ir.smmh.nile.Indexible

interface CanGetAtIndex<T> : Indexible {

    /**
     * @param index Index
     * @return Object at that index
     * @throws IndexOutOfBoundsException If index is invalid
     */
    fun getAtIndex(index: Int): T

    fun getNullableAtIndex(index: Int): T? =
        if (hasIndex(index)) getAtIndex(index) else null

    fun getAtFirstIndex(): T =
        getAtIndex(0)

    fun getNullableAtFirstIndex(): T? =
        getNullableAtIndex(0)

    fun getAtLastIndex(): T =
        getAtIndex(lastIndex)

    fun getNullableAtLastIndex(): T? =
        getNullableAtIndex(lastIndex)

    val singleton: T
        get() {
            assertSingleton()
            return getAtFirstIndex()
        }
}