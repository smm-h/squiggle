package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.abstractalgebra.Structures.BooleanRing
import ir.smmh.math.matrix.Matrix.ValueFunction
import ir.smmh.math.matrix.Matrix.ValueFunction.Independent
import ir.smmh.math.symbolic.TeXable
import ir.smmh.nile.FunctionalSequence
import ir.smmh.nile.Mut
import ir.smmh.nile.Sequential

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
 * - [LowLevelMatrix]: same as array matrix but specialized and more efficient;
 *   uses two-dimensional primitive arrays instead of general object arrays.
 */
interface Matrix<T> : TeXable {
    val rows: Int
    val columns: Int
    val structure: RingLike<T>

    val transpose: Matrix<T>

    val determinant: T?
        get() = if (isSquare) calculatedDeterminant() else null

    private fun calculatedDeterminant(): T {
        // assume isSquare
        if (rows == 2) {
            return structure.subtract(
                structure.multiply(this[0, 0], this[1, 1]),
                structure.multiply(this[0, 1], this[1, 0])
            )
        } else {
            var p: T
            var n: T

            p = structure.addition.identity!!
            n = p

            for (k in 0 until rows step 2)
                p = structure.add(p, structure.multiply(this[0, k], getMinor(0, k).calculatedDeterminant()))

            for (k in 1 until rows step 2)
                n = structure.add(n, structure.multiply(this[0, k], getMinor(0, k).calculatedDeterminant()))

            return structure.subtract(p, n)
        }
    }

    fun getMinor(i: Int, j: Int): Matrix<T> =
        FunctionMatrix.Unmemoized(rows - 1, columns - 1, structure) { x, y ->
            this@Matrix[
                    if (x >= i) x + 1 else x,
                    if (y >= j) y + 1 else y]
        }

    val minorDeterminantMatrix: Matrix<T>
        get() = FunctionMatrix.Memoized(rows, columns, structure) { i, j -> getMinor(i, j).determinant!! }

    val isNatural: Boolean get() = rows > 0 && columns > 0
    val isSquare: Boolean get() = isNatural && rows == columns

    /**
     * [Matrix invertibility](https://en.wikipedia.org/wiki/Invertible_matrix)
     */
    // TODO this is only correct if the ring is commutative
    val isInvertible: Boolean get() = isSquare && structure.invertible(determinant!!)
    val inverse: Matrix<T>? get() = if (isInvertible) minorDeterminantMatrix * structure.invert(determinant!!) else null

    val negative: Matrix<T>
        get() = FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.negate(this@Matrix[i, j]) }

    fun row(i: Int): Sequential<T> = FunctionalSequence(columns) { j -> this[i, j] }
    fun column(j: Int): Sequential<T> = FunctionalSequence(columns) { i -> this[i, j] }

    fun areEqual(that: Matrix<*>): Boolean {
        if (areSameSize(that) && areSameStructure(that)) {
            for (i in 0 until rows)
                for (j in 0 until columns)
                    if (this[i, j] != that[i, j])
                        return false
            return true
        } else return false
    }

    fun areSameSize(that: Matrix<*>): Boolean =
        this.rows == that.rows && this.columns == that.columns

    fun areSameStructure(that: Matrix<*>): Boolean =
        this.structure == that.structure

    operator fun get(i: Int, j: Int): T

    fun <R> convert(structure: RingLike<R>, convertor: (T) -> R): Matrix<R> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> convertor(this[i, j]) }

    operator fun contains(it: T): Boolean {
        for (i in 0 until rows)
            for (j in 0 until columns)
                if (this[i, j] == it)
                    return true
        return false
    }

    class MatrixMultiplicationException(message: String) : Exception(message)

    operator fun times(that: Matrix<T>): Matrix<T> = multiply(that)
    fun multiply(that: Matrix<T>): Matrix<T> =
        multiply(that, MapMatrix<T>(this.rows, that.columns, structure))

    /**
     * [Matrix multiplication](https://en.wikipedia.org/wiki/Matrix_multiplication)
     */
    fun multiply(that: Matrix<T>, destination: Matrix.Mutable<T>): Matrix<T> {
        if (this.structure != that.structure)
            throw MatrixMultiplicationException("structures do not match")
        if (destination.rows != this.rows || destination.columns != that.columns)
            throw MatrixMultiplicationException("incorrect output size")
        val n = columns
        if (that.rows != n)
            throw MatrixMultiplicationException("incorrect input sizes")
        val s0 = structure.addition.identity
        if (s0 == null)
            throw MatrixMultiplicationException("structure has no additive identity")
        destination.setAll { _, i, j ->
            var s: T = s0
            for (k in 0 until n)
                s = structure.add(s, structure.multiply(this[i, k], that[k, j]))
            s
        }
        return destination
    }

    operator fun unaryPlus(): Matrix<T> = this

    operator fun unaryMinus(): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.negate(this[i, j]) }

    operator fun plus(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.add(this[i, j], that[i, j]) }

    operator fun minus(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.subtract(this[i, j], that[i, j]) }

    operator fun times(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.multiply(this[i, j], that) }

    operator fun div(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.divide(this[i, j], that) }

    operator fun rem(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.remainder(this[i, j], that) }

    private fun plusInverse(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.add(that[i, j], this[i, j]) }

    private fun minusInverse(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.subtract(that[i, j], this[i, j]) }

    private fun timesInverse(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.multiply(that, this[i, j]) }

    private fun divInverse(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.divide(that, this[i, j]) }

    private fun remInverse(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> structure.remainder(that, this[i, j]) }

    interface Mutable<T> : Matrix<T> { //, Mut.Able {

        fun createSimilar(): Matrix.Mutable<T> = createSameStructure(rows, columns)
        fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T>
        override val transpose: Matrix<T> get() = createSameStructure(columns, rows).setTransposed(this)

        operator fun set(i: Int, j: Int, value: T)

        fun setTransposed(source: Matrix<T>) =
            setAll { _, i, j -> source[j, i] }

        fun setAll(f: ValueFunction<T>) =
            also { for (i in 0 until rows) for (j in 0 until columns) this[i, j] = f(this, i, j) }

        fun setAll(value: T) =
            also { for (i in 0 until rows) for (j in 0 until columns) this[i, j] = value }

        fun setPartially(i1: Int, i2: Int, j1: Int, j2: Int, f: ValueFunction<T>) =
            also { for (i in i1 until i2) for (j in j1 until j2) this[i, j] = f(this, i, j) }

        fun setPartially(i1: Int, i2: Int, j1: Int, j2: Int, value: T) =
            also { for (i in i1 until i2) for (j in j1 until j2) this[i, j] = value }

        fun setSpecific(s: Iterable<Pair<Int, Int>>, f: ValueFunction<T>) =
            also { for ((i, j) in s) this[i, j] = f(this, i, j) }

        fun setSpecific(s: Iterable<Pair<Int, Int>>, value: T) =
            also { for ((i, j) in s) this[i, j] = value }

        fun setAllIf(c: Condition, f: ValueFunction<T>) =
            also { for (i in 0 until rows) for (j in 0 until columns) if (c(i, j)) this[i, j] = f(this, i, j) }

        fun setAllIf(value: T, c: Condition) =
            also { for (i in 0 until rows) for (j in 0 until columns) if (c(i, j)) this[i, j] = value }

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

    fun uniform(value: T) = UniformMatrix(rows, columns, structure, value)

    fun pair(i: Int, j: Int): Int = i * rows + j
    fun unpairI(x: Int): Int = x / rows
    fun unpairJ(x: Int): Int = x % rows
    fun unpair(x: Int): Pair<Int, Int> = unpairI(x) to unpairJ(x)

    override val render: String
        get() = (0 until rows).joinToString(" \\\\\n", "\\begin{bmatrix}\n", "\n\\end{bmatrix}") {
            row(it).joinToString(" & ")
        }

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

        val rowMajor = ValueFunction<Int> { m, i, j -> i * m.columns + j + 1 }
        val columnMajor = ValueFunction<Int> { m, i, j -> j * m.rows + i + 1 }
        val multiplicationTable = ValueFunction.Independent<Int> { i, j -> (i + 1) * (j + 1) }

        fun identity(n: Int): Matrix<Boolean> =
            FunctionMatrix.Unmemoized(n, n, BooleanRing) { i, j -> i == j }

        fun <T> of(rows: Int, columns: Int, structure: RingLike<T>, vararg values: T): Matrix<T> =
            MapMatrix(rows, columns, structure).setAll { _, i, j -> values[i * columns + j] }

        fun hashCode(m: Matrix<*>): Int {
            return toString(m).hashCode() xor m.structure.hashCode()
        }

        fun <T> toString(m: Matrix<T>): String = StringBuilder().apply {
            val n = m.rows - 1
            for (i in 0 until m.rows) {
                if (i != 0) append('\n')
                append(
                    when (i) {
                        0 -> '┌'
                        n -> '└'
                        else -> '│'
                    }
                )
                append('\t')
                for (j in 0 until m.columns) {
                    if (j != 0) append('\t')
                    append(m[i, j])
                }
                append('\t')
                append(
                    when (i) {
                        0 -> '┐'
                        n -> '┘'
                        else -> '│'
                    }
                )
            }
        }.toString()
    }
}