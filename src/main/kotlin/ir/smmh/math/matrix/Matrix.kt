package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.abstractalgebra.Structures.BooleanRing
import ir.smmh.math.matrix.Matrix.ValueFunction
import ir.smmh.math.matrix.Matrix.ValueFunction.Independent
import ir.smmh.nile.Mut

/**
 * You can choose from various different classes of matrices:
 *
 * - [ArrayMatrix]: read/write is very fast, but because arrays are contiguous
 *   allocation is slow and memory usage is high; good for low-level tasks
 * - [FunctionMatrix.Unmemoized]: no write, read is as fast as function call,
 *   no memory used at all; good for fast functions
 * - [FunctionMatrix.Memoized]: no write, read is as fast as a map look-up,
 *   used memory increases the more it is queried; good for slow functions
 * - [MapMatrix]: read/write is slower than array but faster than function,
 *   however because maps are not contiguous, allocation of even huge matrices
 *   is fast and memory usage only increase when values are different from a
 *   given default value; good for sparse matrices and high-level tasks
 * - [UniformMatrix]: no write, read is instantanious because it is a constant
 */
interface Matrix<T> {
    val width: Int
    val height: Int
    val structure: RingLike<T>

    operator fun get(i: Int, j: Int): T

    fun <R> convert(structure: RingLike<R>, convertor: (T) -> R): Matrix<R> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> convertor(this[i, j]) }

    operator fun contains(it: T): Boolean {
        for (i in 0 until width)
            for (j in 0 until height)
                if (this[i, j] == it)
                    return true
        return false
    }

    operator fun unaryPlus(): Matrix<T> = this

    operator fun unaryMinus(): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.negate(this[i, j]) }

    operator fun plus(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.add(this[i, j], that[i, j]) }

    operator fun minus(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.subtract(this[i, j], that[i, j]) }

    operator fun times(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.multiply(this[i, j], that) }

    operator fun div(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.divide(this[i, j], that) }

    operator fun rem(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.remainder(this[i, j], that) }

    private fun plusInverse(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.add(that[i, j], this[i, j]) }

    private fun minusInverse(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.subtract(that[i, j], this[i, j]) }

    private fun timesInverse(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.multiply(that, this[i, j]) }

    private fun divInverse(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.divide(that, this[i, j]) }

    private fun remInverse(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(width, height, structure) { i, j -> structure.remainder(that, this[i, j]) }

    interface Mutable<T> : Matrix<T>, Mut.Able {

        operator fun set(i: Int, j: Int, value: T)

        fun setAll(f: ValueFunction<T>) =
            also { for (i in 0 until width) for (j in 0 until height) this[i, j] = f(this, i, j) }

        fun setAll(value: T) =
            also { for (i in 0 until width) for (j in 0 until height) this[i, j] = value }

        fun setPartially(i1: Int, i2: Int, j1: Int, j2: Int, f: ValueFunction<T>) =
            also { for (i in i1 until i2) for (j in j1 until j2) this[i, j] = f(this, i, j) }

        fun setPartially(i1: Int, i2: Int, j1: Int, j2: Int, value: T) =
            also { for (i in i1 until i2) for (j in j1 until j2) this[i, j] = value }

        fun setSpecific(s: Iterable<Pair<Int, Int>>, f: ValueFunction<T>) =
            also { for ((i, j) in s) this[i, j] = f(this, i, j) }

        fun setSpecific(s: Iterable<Pair<Int, Int>>, value: T) =
            also { for ((i, j) in s) this[i, j] = value }

        fun setAllIf(c: Condition, f: ValueFunction<T>) =
            also { for (i in 0 until width) for (j in 0 until height) if (c(i, j)) this[i, j] = f(this, i, j) }

        fun setAllIf(value: T, c: Condition) =
            also { for (i in 0 until width) for (j in 0 until height) if (c(i, j)) this[i, j] = value }

        fun setPartiallyIf(i1: Int, i2: Int, j1: Int, j2: Int, f: ValueFunction<T>, c: Condition) =
            also { for (i in i1 until i2) for (j in j1 until j2) if (c(i, j)) this[i, j] = f(this, i, j) }

        fun setPartiallyIf(i1: Int, i2: Int, j1: Int, j2: Int, value: T, c: Condition) =
            also { for (i in i1 until i2) for (j in j1 until j2) if (c(i, j)) this[i, j] = value }

        fun setSpecificIf(s: Iterable<Pair<Int, Int>>, f: ValueFunction<T>, c: Condition) =
            also { for ((i, j) in s) if (c(i, j)) this[i, j] = f(this, i, j) }

        fun setSpecificIf(s: Iterable<Pair<Int, Int>>, value: T, c: Condition) =
            also { for ((i, j) in s) if (c(i, j)) this[i, j] = value }
    }

    fun interface Condition : (Int, Int) -> Boolean

    fun interface ValueFunction<T> : (Matrix<T>, Int, Int) -> T {
        fun interface Independent<T> : (Int, Int) -> T, ValueFunction<T> {
            override fun invoke(m: Matrix<T>, i: Int, j: Int): T = invoke(i, j)
        }

        fun toIndependent(m: Matrix<T>): Independent<T> =
            Independent { i, j -> invoke(m, i, j) }
    }

    fun uniform(value: T) = UniformMatrix(width, height, structure, value)

    companion object {

        operator fun Int.times(m: Matrix<Int>) = m.timesInverse(this)
        operator fun Long.times(m: Matrix<Long>) = m.timesInverse(this)
        operator fun Float.times(m: Matrix<Float>) = m.timesInverse(this)
        operator fun Double.times(m: Matrix<Double>) = m.timesInverse(this)

        operator fun Int.div(m: Matrix<Int>) = m.divInverse(this)
        operator fun Long.div(m: Matrix<Long>) = m.divInverse(this)
        operator fun Float.div(m: Matrix<Float>) = m.divInverse(this)
        operator fun Double.div(m: Matrix<Double>) = m.divInverse(this)

        operator fun Int.rem(m: Matrix<Int>) = m.remInverse(this)
        operator fun Long.rem(m: Matrix<Long>) = m.remInverse(this)
        operator fun Float.rem(m: Matrix<Float>) = m.remInverse(this)
        operator fun Double.rem(m: Matrix<Double>) = m.remInverse(this)

        val rowMajor = ValueFunction<Int> { m, i, j -> i * m.width + j + 1 }
        val columnMajor = ValueFunction<Int> { m, i, j -> j * m.height + i + 1 }
        val multiplicationTable = ValueFunction.Independent<Int> { i, j -> (i + 1) * (j + 1) }

        fun identity(n: Int): Matrix<Boolean> =
            FunctionMatrix.Unmemoized(n, n, BooleanRing) { i, j -> i == j }
    }
}