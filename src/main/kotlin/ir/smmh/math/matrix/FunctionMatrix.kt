package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.nile.Cache
import ir.smmh.math.MathematicalObject as M

sealed class FunctionMatrix<T : M>(
    override val rows: Int,
    override val columns: Int,
    override val ring: RingLikeStructure.SubtractionRing<T>,
    protected val function: (Int, Int) -> T,
) : AbstractMatrix<T>() {

    override val transpose: Matrix<T> by lazy {
        FunctionMatrix.Unmemoized(columns, rows, ring) { i, j -> this[j, i] }
    }

    class Unmemoized<T : M>(
        rows: Int,
        columns: Int,
        structure: RingLikeStructure.SubtractionRing<T>,
        function: (Int, Int) -> T,
    ) : FunctionMatrix<T>(rows, columns, structure, function) {
        override fun get(i: Int, j: Int): T = function(i, j)
    }

    class Memoized<T : M>(
        rows: Int,
        columns: Int,
        structure: RingLikeStructure.SubtractionRing<T>,
        function: (Int, Int) -> T,
    ) : FunctionMatrix<T>(rows, columns, structure, function) {
        private val cache = Cache<Int, T> { x -> function(unpairI(x), unpairJ(x)) }
        override fun get(i: Int, j: Int): T = cache(pair(i, j))
    }
}