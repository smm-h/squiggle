package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.nile.Cache

sealed class FunctionMatrix<T>(
    override val rows: Int,
    override val columns: Int,
    override val structure: RingLike<T>,
    protected val function: Matrix.ValueFunction.Independent<T>,
) : AbstractMatrix<T>() {

    override val transpose: Matrix<T> by lazy {
        FunctionMatrix.Unmemoized(columns, rows, structure) { i, j -> this[j, i] }
    }

    class Unmemoized<T>(
        rows: Int,
        columns: Int,
        structure: RingLike<T>,
        function: Matrix.ValueFunction.Independent<T>,
    ) : FunctionMatrix<T>(rows, columns, structure, function) {
        override fun get(i: Int, j: Int): T = function(i, j)
    }

    class Memoized<T>(
        rows: Int,
        columns: Int,
        structure: RingLike<T>,
        function: Matrix.ValueFunction.Independent<T>,
    ) : FunctionMatrix<T>(rows, columns, structure, function) {
        private val cache = Cache<Int, T> { x -> function(unpairI(x), unpairJ(x)) }
        override fun get(i: Int, j: Int): T = cache(pair(i, j))
    }
}