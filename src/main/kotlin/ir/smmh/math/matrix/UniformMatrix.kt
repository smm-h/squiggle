package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.MathematicalObject as M

class UniformMatrix<T : M>(
    override val rows: Int,
    override val columns: Int,
    override val ring: RingLikeStructure.SubtractionRing<T>,
    val value: T,
) : AbstractMatrix<T>() {
    override val transpose: Matrix<T> = this
    override fun get(i: Int, j: Int): T = value
}