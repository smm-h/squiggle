package ir.smmh.math.matrix

import ir.smmh.mage.core.Color
import ir.smmh.mage.core.Image
import ir.smmh.mage.core.Platform
import ir.smmh.mage.core.Size
import ir.smmh.math.MathematicalException
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.logic.BooleanCalculator
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.numbers.BuiltinNumberType
import ir.smmh.math.numbers.Numbers
import ir.smmh.nile.FunctionalSequence
import ir.smmh.nile.Sequential
import ir.smmh.nile.verbs.CanChangeValues
import ir.smmh.math.MathematicalObject as M


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
interface Matrix<T : M> : M {

    /** [rows] > 0 */
    val rows: Int

    /** [columns] > 0 */
    val columns: Int

    val ring: RingLikeStructure.SubtractionRing<T>

    val transpose: Matrix<T>

    class UndeterminableMatrixException(reason: String) :
        MathematicalException("matrix is not determinable because: $reason")

    val isSquare: Boolean get() = rows == columns

    val determinant: T
        get() =
            if (isSquare) calculatedDeterminant()
            else throw UndeterminableMatrixException("it is not square")

    val minorDeterminantMatrix: Matrix<T>
        get() =
            if (isSquare) FunctionMatrix.Memoized(rows, columns, ring) { i, j -> getMinor(i, j).determinant }
            else throw UndeterminableMatrixException("it is not square")

    private fun calculatedDeterminant(): T {
        // assume isSquare
        if (rows == 2) {
            return ring.subtract(
                ring.multiply(get(0, 0), get(1, 1)),
                ring.multiply(get(0, 1), get(1, 0))
            )
        } else {
            var p: T
            var n: T

            p = ring.additiveGroup.identityElement
            n = p

            for (k in 0 until rows step 2)
                p = ring.add(p, ring.multiply(get(0, k), getMinor(0, k).calculatedDeterminant()))

            for (k in 1 until rows step 2)
                n = ring.add(n, ring.multiply(get(0, k), getMinor(0, k).calculatedDeterminant()))

            return ring.subtract(p, n)
        }
    }

    fun getMinor(i: Int, j: Int): Matrix<T> =
        FunctionMatrix.Unmemoized(rows - 1, columns - 1, ring) { x, y ->
            get(
                if (x >= i) x + 1 else x,
                if (y >= j) y + 1 else y,
            )
        }

    /**
     * [Matrix invertibility](https://en.wikipedia.org/wiki/Invertible_matrix)
     */
    val inverse: Matrix<T>
        get() {
            val r = ring

            if (r !is RingLikeStructure.CommutativeRing<T>)
                throw UninvertibleMatrixException("its ring is not commutative")

            if (r !is RingLikeStructure.DivisionRing<T>)
                throw UninvertibleMatrixException("its ring does not support division")

            return minorDeterminantMatrix * r.reciprocal(determinant)
        }

    class UninvertibleMatrixException(reason: String) :
        MathematicalException("matrix is not invertible because: $reason")

    val negative: Matrix<T>
        get() = FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.negate(get(i, j)) }

    fun row(i: Int): Sequential<T> = FunctionalSequence(columns) { j -> get(i, j) }
    fun column(j: Int): Sequential<T> = FunctionalSequence(columns) { i -> get(i, j) }

    override fun isNonReferentiallyEqualTo(that: ir.smmh.math.MathematicalObject): Knowable {
        if (that is Matrix<*> && areSameSize(that) && areSameStructure(that)) {
            for (i in 0 until rows)
                for (j in 0 until columns)
                    if (this[i, j] != that[i, j])
                        return Logical.False
            return Logical.True
        } else return Logical.False
    }

    fun areSameSize(that: Matrix<*>): Boolean =
        rows == that.rows && columns == that.columns

    fun areSameStructure(that: Matrix<*>): Boolean =
        ring == that.ring

    operator fun get(i: Int, j: Int): T

    fun <R : M> convert(structure: RingLikeStructure.SubtractionRing<R>, convertor: (T) -> R): Matrix<R> =
        FunctionMatrix.Unmemoized(rows, columns, structure) { i, j -> convertor(get(i, j)) }

    operator fun contains(it: T): Boolean {
        for (i in 0 until rows)
            for (j in 0 until columns)
                if (get(i, j) == it)
                    return true
        return false
    }

    operator fun times(that: Matrix<T>): Matrix<T> = multiply(that)
    fun multiply(that: Matrix<T>): Matrix<T> =
        multiply(that, MapMatrix<T>(rows, that.columns, ring))

    /**
     * [Matrix multiplication](https://en.wikipedia.org/wiki/Matrix_multiplication)
     */
    fun multiply(that: Matrix<T>, destination: Matrix.Mutable<T>): Matrix<T> {
        if (!areSameStructure(that))
            throw MatrixMultiplicationException("structures do not match")
        if (destination.rows != this.rows || destination.columns != that.columns)
            throw MatrixMultiplicationException("incorrect output size")
        val n = columns
        if (that.rows != n)
            throw MatrixMultiplicationException("incorrect input sizes")
        val s0 = ring.additiveGroup.identityElement
        destination.setAll { i, j ->
            var s: T = s0
            for (k in 0 until n)
                s = ring.add(s, ring.multiply(this[i, k], that[k, j]))
            s
        }
        return destination
    }

    operator fun unaryPlus(): Matrix<T> = this

    operator fun plus(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.add(this[i, j], that[i, j]) }

    operator fun times(that: T): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.multiply(this[i, j], that) }

    operator fun unaryMinus(): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.negate(this[i, j]) }

    operator fun minus(that: Matrix<T>): Matrix<T> =
        FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.subtract(this[i, j], that[i, j]) }

//    TODO: these operations
//    operator fun div(that: T): Matrix<T> =
//        FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.divide(this[i, j], that) }
//    operator fun rem(that: T): Matrix<T> =
//        FunctionMatrix.Unmemoized(rows, columns, ring) { i, j -> ring.remainder(this[i, j], that) }

    interface Mutable<T : M> : Matrix<T>, CanChangeValues {

        override val transpose: Matrix.Mutable<T>

        fun setWithoutMutation(i: Int, j: Int, value: T)

        operator fun set(i: Int, j: Int, value: T) {
            changesToValues.beforeChange()
            setWithoutMutation(i, j, value)
            changesToValues.afterChange()
        }

        fun setAll(f: (Int, Int) -> T) =
            also {
                changesToValues.beforeChange()
                for (i in 0 until rows) for (j in 0 until columns) setWithoutMutation(i, j, f(i, j))
                changesToValues.afterChange()
            }

        fun setAll(value: T) =
            also {
                changesToValues.beforeChange()
                for (i in 0 until rows) for (j in 0 until columns) setWithoutMutation(i, j, value)
                changesToValues.afterChange()
            }

        fun setPartially(i1: Int, i2: Int, j1: Int, j2: Int, f: (Int, Int) -> T) =
            also {
                val iRange = i1 until i2
                val jRange = j1 until j2
                if (!iRange.isEmpty() && !jRange.isEmpty()) {
                    changesToValues.beforeChange()
                    for (i in iRange) for (j in jRange) setWithoutMutation(i, j, f(i, j))
                    changesToValues.afterChange()
                }
            }

        fun setPartially(i1: Int, i2: Int, j1: Int, j2: Int, value: T) =
            also {
                val iRange = i1 until i2
                val jRange = j1 until j2
                if (!iRange.isEmpty() && !jRange.isEmpty()) {
                    changesToValues.beforeChange()
                    for (i in iRange) for (j in jRange) setWithoutMutation(i, j, value)
                    changesToValues.afterChange()
                }
            }

        fun setSpecific(s: Iterable<Pair<Int, Int>>, f: (Int, Int) -> T) =
            also {
                changesToValues.beforeChange()
                for ((i, j) in s) setWithoutMutation(i, j, f(i, j))
                changesToValues.afterChange()
            }

        fun setSpecific(s: Iterable<Pair<Int, Int>>, value: T) =
            also {
                changesToValues.beforeChange()
                for ((i, j) in s) setWithoutMutation(i, j, value)
                changesToValues.afterChange()
            }

        fun setAllIf(c: Condition, f: (Int, Int) -> T) =
            also {
                changesToValues.beforeChange()
                for (i in 0 until rows) for (j in 0 until columns) if (c(i, j)) setWithoutMutation(i, j, f(i, j))
                changesToValues.afterChange()
            }

        fun setAllIf(value: T, c: Condition) =
            also {
                changesToValues.beforeChange()
                for (i in 0 until rows) for (j in 0 until columns) if (c(i, j)) setWithoutMutation(i, j, value)
                changesToValues.afterChange()
            }

        fun setPartiallyIf(i1: Int, i2: Int, j1: Int, j2: Int, f: (Int, Int) -> T, c: Condition) =
            also {
                val iRange = i1 until i2
                val jRange = j1 until j2
                if (!iRange.isEmpty() && !jRange.isEmpty()) {
                    changesToValues.beforeChange()
                    for (i in iRange) for (j in jRange) if (c(i, j)) setWithoutMutation(i, j, f(i, j))
                    changesToValues.afterChange()
                }
            }

        fun setPartiallyIf(i1: Int, i2: Int, j1: Int, j2: Int, value: T, c: Condition) =
            also {
                val iRange = i1 until i2
                val jRange = j1 until j2
                if (!iRange.isEmpty() && !jRange.isEmpty()) {
                    changesToValues.beforeChange()
                    for (i in iRange) for (j in jRange) if (c(i, j)) setWithoutMutation(i, j, value)
                    changesToValues.afterChange()
                }
            }

        fun setSpecificIf(s: Iterable<Pair<Int, Int>>, f: (Int, Int) -> T, c: Condition) =
            also {
                changesToValues.beforeChange()
                for ((i, j) in s) if (c(i, j)) setWithoutMutation(i, j, f(i, j))
                changesToValues.afterChange()
            }

        fun setSpecificIf(s: Iterable<Pair<Int, Int>>, value: T, c: Condition) =
            also {
                changesToValues.beforeChange()
                for ((i, j) in s) if (c(i, j)) setWithoutMutation(i, j, value)
                changesToValues.afterChange()
            }

        fun setTransposed(source: Matrix<T>) =
            if (isSquare) setAll { i, j -> source[j, i] }
            else throw NonSquareTranspositionException()
    }

    class NonSquareTranspositionException :
        MathematicalException("cannot transpose a non-square matrix")

    fun interface Condition : (Int, Int) -> Boolean

    object ValueFunction {
        fun <T> independent(f: () -> T): (Int, Int) -> T = { _, _ -> f() }
        fun <T> constant(c: T): (Int, Int) -> T = { _, _ -> c }
    }

    fun uniform(value: T) = UniformMatrix(rows, columns, ring, value)

    fun pair(i: Int, j: Int): Int = i * rows + j
    fun unpairI(p: Int): Int = p / rows
    fun unpairJ(p: Int): Int = p % rows
    fun unpair(p: Int): Pair<Int, Int> = unpairI(p) to unpairJ(p)

    fun toImage(platform: Platform, zoom: Int = 1, colorer: (T) -> Color.Packed): Image =
        platform.createGraphics(Size.of(rows * zoom, columns * zoom)).apply {
            val z = zoom.toDouble()
            fill = true
            for (i in 0 until rows) {
                for (j in 0 until columns) {
                    color = colorer(get(i, j))
                    rectangle(i * z, j * z, z, z)
                }
            }
        }.toImage()

    companion object {

        fun getRowMajor(columns: Int): (Int, Int) -> Numbers.Integer =
            { i, j -> BuiltinNumberType.IntInteger(j + 1 + i * columns) }

        fun getColumnMajor(rows: Int): (Int, Int) -> Numbers.Integer =
            { i, j -> BuiltinNumberType.IntInteger(i + 1 + j * rows) }

        fun identity(n: Int): Matrix<Logical> =
            FunctionMatrix.Unmemoized(n, n, Logical.Structure.asRing) { i, j -> Logical.of(i == j) }

        fun <T : M> of(
            rows: Int,
            columns: Int,
            structure: RingLikeStructure.SubtractionRing<T>,
            values: List<T>
        ): Matrix<T> =
            MapMatrix(rows, columns, structure).setAll { i, j -> values[i * columns + j] }

        inline fun <T : M> Matrix<T>.forEach(action: (Int, Int) -> Unit) {
            val r = 0 until rows
            val c = 0 until columns
            for (i in r) for (j in c) action(i, j)
        }
    }
}