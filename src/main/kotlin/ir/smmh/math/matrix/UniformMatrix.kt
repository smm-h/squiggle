package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike

class UniformMatrix<T>(
    override val rows: Int,
    override val columns: Int,
    override val structure: RingLike<T>,
    val value: T,
) : AbstractMatrix<T>() {
    override val transpose: Matrix<T> = this
    override fun get(i: Int, j: Int): T = value
}