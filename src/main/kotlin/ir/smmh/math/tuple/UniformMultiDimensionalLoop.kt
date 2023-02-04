package ir.smmh.math.tuple

import ir.smmh.math.MathematicalObject as M


object UniformMultiDimensionalLoop {
    fun <T : M> loop(
        dimensions: Int,
        iterable: Iterable<T>,
        block: (Tuple.Uniform<T>) -> Unit,
    ) = loop(dimensions, { iterable }, block)

    fun <T : M> loop(
        dimensions: Int,
        iterables: (Int) -> Iterable<T>,
        block: (Tuple.Uniform<T>) -> Unit,
    ) = if (dimensions > 0) loop(dimensions, iterables, block, MutableArrayTuple(dimensions)) else Unit

    private fun <T : M> loop(
        dimensions: Int,
        iterables: (Int) -> Iterable<T>,
        block: (Tuple.Uniform<T>) -> Unit,
        tuple: MutableArrayTuple<T>,
    ) {
        if (dimensions > 1) {
            val n = dimensions - 1
            val i = tuple.length - dimensions
            for (x in iterables(i)) {
                tuple[i] = x
                loop(n, iterables, block, tuple)
            }
        } else {
            val i = tuple.length - 1
            for (x in iterables(i)) {
                tuple[i] = x
                block(tuple)
            }
        }
    }
}