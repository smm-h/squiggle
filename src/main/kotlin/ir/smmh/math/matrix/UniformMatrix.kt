package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike

class UniformMatrix<T>(
    override val width: Int,
    override val height: Int,
    override val structure: RingLike<T>,
    val value: T,
) : BaseMatrix<T>() {
    override fun get(i: Int, j: Int): T = value
}