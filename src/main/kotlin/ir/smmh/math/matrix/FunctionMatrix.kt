package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.nile.Cache

sealed class FunctionMatrix<T>(
    override val width: Int,
    override val height: Int,
    override val structure: RingLike<T>,
    protected val function: Matrix.ValueFunction.Independent<T>,
) : BaseMatrix<T>() {

    abstract val isMemoized: Boolean

    class Unmemoized<T>(
        width: Int,
        height: Int,
        structure: RingLike<T>,
        function: Matrix.ValueFunction.Independent<T>,
    ) : FunctionMatrix<T>(width, height, structure, function) {
        override fun get(i: Int, j: Int): T = function(i, j)
        override val isMemoized: Boolean = false
    }

    class Memoized<T>(
        width: Int,
        height: Int,
        structure: RingLike<T>,
        function: Matrix.ValueFunction.Independent<T>,
    ) : FunctionMatrix<T>(width, height, structure, function) {
        private val cache = Cache<Pair<Int, Int>, T> { function(it.first, it.second) }
        override fun get(i: Int, j: Int): T = cache(Pair(i, j))
        override val isMemoized: Boolean = true
    }
}