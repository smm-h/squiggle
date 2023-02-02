package ir.smmh.math.matrix

import ir.smmh.math.MathematicalObject
import ir.smmh.math.abstractalgebra.RingLikeStructure

class UniformMatrix<T : MathematicalObject>(
    override val rows: Int,
    override val columns: Int,
    override val ring: RingLikeStructure.SubtractionRing<T>,
    val value: T,
) : AbstractMatrix<T>() {
    override val transpose: Matrix<T> = this
    override fun get(i: Int, j: Int): T = value
}