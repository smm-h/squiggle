package ir.smmh.math.vector

import ir.smmh.math.matrix.Matrix

interface Vector<T> : Matrix<T> {
    val length: Int
    override val rows: Int get() = length
    override val columns: Int get() = 1
    operator fun get(i: Int): T
    override fun get(i: Int, j: Int): T = this[i]

    interface Mutable<T> : Vector<T>, Matrix.Mutable<T> {
        operator fun set(i: Int, value: T)
        override fun set(i: Int, j: Int, value: T) = set(i, value)
    }
}


