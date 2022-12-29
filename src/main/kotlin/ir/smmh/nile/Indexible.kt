package ir.smmh.nile

import ir.smmh.nile.verbs.CanContainPlace

interface Indexible : CanContainPlace<Int> {

    val lastIndex: Int get() = size - 1

    fun wrapIndex(index: Int, ifEmpty: Int) = if (size == 0) ifEmpty else {
        var i = index
        while (i < 0)
            i += size
        while (i > size)
            i -= size
        i
    }

    fun hasIndex(index: Int) = index in 0 until size

    fun doesNotHaveIndex(index: Int) = !hasIndex(index)

    fun validateIndex(index: Int) {
        if (doesNotHaveIndex(index)) throw IndexOutOfBoundsException("$index/$size")
    }

    fun validateBetweenIndices(index: Int) {
        if (index !in 0..size) throw IndexOutOfBoundsException("$index/$size")
    }

    override fun containsPlace(toCheck: Int) = hasIndex(toCheck)
}