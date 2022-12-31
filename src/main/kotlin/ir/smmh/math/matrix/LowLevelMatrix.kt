package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures
import kotlin.Boolean as kBoolean
import kotlin.Double as kDouble
import kotlin.Float as kFloat
import kotlin.Int as kInt
import kotlin.Long as kLong

sealed class LowLevelMatrix<T : Any> : AbstractMatrix.Mutable<T>() {

    override val changesToValues
        get() = throw UnsupportedOperationException("LowLevelMatrix does not support changesToValues")

    class Int(
        override val rows: kInt,
        override val columns: kInt,
        valueFunction: ((kInt, kInt) -> kInt)?,
    ) : LowLevelMatrix<kInt>() {
        override val structure = Structures.Integer32Ring
        override fun createSameStructure(rows: kInt, columns: kInt): Matrix.Mutable<kInt> =
            Int(columns, rows, null)

        private val array = Array<IntArray>(rows) { IntArray(columns) }
        override fun get(i: kInt, j: kInt): kInt = array[i][j]
        override fun set(i: kInt, j: kInt, value: kInt) {
            array[i][j] = value
        }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(i, j)
        }

        override fun multiply(that: Matrix<kInt>): Matrix<kInt> = Int(rows, columns) { i, j ->
            var s = 0
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Long(
        override val rows: kInt,
        override val columns: kInt,
        valueFunction: ((kInt, kInt) -> kLong)?,
    ) : LowLevelMatrix<kLong>() {
        override val structure = Structures.Integer64Ring
        override fun createSameStructure(rows: kInt, columns: kInt): Matrix.Mutable<kLong> =
            Long(columns, rows, null)

        private val array = Array<LongArray>(rows) { LongArray(columns) }
        override fun get(i: kInt, j: kInt): kLong = array[i][j]
        override fun set(i: kInt, j: kInt, value: kLong) {
            array[i][j] = value
        }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(i, j)
        }

        override fun multiply(that: Matrix<kLong>): Matrix<kLong> = Long(rows, columns) { i, j ->
            var s = 0L
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Double(
        override val rows: kInt,
        override val columns: kInt,
        valueFunction: ((kInt, kInt) -> kDouble)?,
    ) : LowLevelMatrix<kDouble>() {
        override val structure = Structures.FloatingPoint64Field
        override fun createSameStructure(rows: kInt, columns: kInt): Matrix.Mutable<kDouble> =
            Double(columns, rows, null)

        private val array = Array<DoubleArray>(rows) { DoubleArray(columns) }
        override fun get(i: kInt, j: kInt): kDouble = array[i][j]
        override fun set(i: kInt, j: kInt, value: kDouble) {
            array[i][j] = value
        }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(i, j)
        }

        override fun multiply(that: Matrix<kDouble>): Matrix<kDouble> = Double(rows, columns) { i, j ->
            var s = 0.0
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Float(
        override val rows: kInt,
        override val columns: kInt,
        valueFunction: ((kInt, kInt) -> kFloat)?,
    ) : LowLevelMatrix<kFloat>() {
        override val structure = Structures.FloatingPoint32Field
        override fun createSameStructure(rows: kInt, columns: kInt): Matrix.Mutable<kFloat> =
            Float(columns, rows, null)

        private val array = Array<FloatArray>(rows) { FloatArray(columns) }
        override fun get(i: kInt, j: kInt): kFloat = array[i][j]
        override fun set(i: kInt, j: kInt, value: kFloat) {
            array[i][j] = value
        }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(i, j)
        }

        override fun multiply(that: Matrix<kFloat>): Matrix<kFloat> = Float(rows, columns) { i, j ->
            var s = 0F
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Boolean(
        override val rows: kInt,
        override val columns: kInt,
        valueFunction: ((kInt, kInt) -> kBoolean)?,
    ) : LowLevelMatrix<kBoolean>() {
        override val structure = Structures.BooleanRing
        override fun createSameStructure(rows: kInt, columns: kInt): Matrix.Mutable<kBoolean> =
            Boolean(columns, rows, null)

        private val array = Array<BooleanArray>(rows) { BooleanArray(columns) }
        override fun get(i: kInt, j: kInt): kBoolean = array[i][j]
        override fun set(i: kInt, j: kInt, value: kBoolean) {
            array[i][j] = value
        }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(i, j)
        }

        override fun multiply(that: Matrix<kBoolean>): Matrix<kBoolean> = Boolean(rows, columns) { i, j ->
            var s = false
            for (k in 0 until columns) s = s xor (this[i, k] && that[k, j])
            s
        }
    }
}