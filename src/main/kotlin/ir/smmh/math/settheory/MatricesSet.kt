package ir.smmh.math.settheory

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.matrix.FunctionMatrix
import ir.smmh.math.matrix.Matrix
import ir.smmh.nile.Cache

class MatricesSet<T : Any> private constructor(
    val rows: Int,
    val columns: Int,
    val structure: RingLike<T>,
) : Set.Specific<Matrix<T>> {
    override val choose: () -> Matrix<T> =
        { FunctionMatrix.Memoized(rows, columns, structure) { _, _ -> structure.domain.choose() } }

    override fun containsSpecific(it: Matrix<T>): Boolean =
        it.rows == rows && it.columns == columns && it.structure == structure

    companion object {
        private val cache = Cache<Triple<Int, Int, RingLike<*>>, MatricesSet<*>> {
            MatricesSet(it.first, it.second, it.third)
        }

        @Suppress("UNCHECKED_CAST")
        fun <T : Any> of(rows: Int, columns: Int, structure: RingLike<T>): MatricesSet<T> =
            cache(Triple(rows, columns, structure)) as MatricesSet<T>
    }
}