package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures

sealed class LowLevelMatrix<T> : AbstractMatrix<T>() {

    class Int(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        valueFunction: Matrix.ValueFunction.Independent<kotlin.Int>?,
    ) : LowLevelMatrix<kotlin.Int>() {
        override val structure = Structures.Integer32Ring
        private val array = Array<IntArray>(rows) { IntArray(columns) }
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Int = array[i][j]
        override val transpose: Matrix<kotlin.Int> by lazy { Int(columns, rows) { i, j -> this[j, i] } }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(this, i, j)
        }

        override fun multiply(that: Matrix<kotlin.Int>): Matrix<kotlin.Int> = Int(rows, columns) { i, j ->
            var s = 0
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Long(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        valueFunction: Matrix.ValueFunction.Independent<kotlin.Long>?,
    ) : LowLevelMatrix<kotlin.Long>() {
        override val structure = Structures.Integer64Ring
        private val array = Array<LongArray>(rows) { LongArray(columns) }
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Long = array[i][j]
        override val transpose: Matrix<kotlin.Long> by lazy { Long(columns, rows) { i, j -> this[j, i] } }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(this, i, j)
        }

        override fun multiply(that: Matrix<kotlin.Long>): Matrix<kotlin.Long> = Long(rows, columns) { i, j ->
            var s = 0L
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Double(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        valueFunction: Matrix.ValueFunction.Independent<kotlin.Double>?,
    ) : LowLevelMatrix<kotlin.Double>() {
        override val structure = Structures.RealDPField
        private val array = Array<DoubleArray>(rows) { DoubleArray(columns) }
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Double = array[i][j]
        override val transpose: Matrix<kotlin.Double> by lazy { Double(columns, rows) { i, j -> this[j, i] } }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(this, i, j)
        }

        override fun multiply(that: Matrix<kotlin.Double>): Matrix<kotlin.Double> = Double(rows, columns) { i, j ->
            var s = 0.0
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Float(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        valueFunction: Matrix.ValueFunction.Independent<kotlin.Float>?,
    ) : LowLevelMatrix<kotlin.Float>() {
        override val structure = Structures.RealFPField
        private val array = Array<FloatArray>(rows) { FloatArray(columns) }
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Float = array[i][j]
        override val transpose: Matrix<kotlin.Float> by lazy { Float(columns, rows) { i, j -> this[j, i] } }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(this, i, j)
        }

        override fun multiply(that: Matrix<kotlin.Float>): Matrix<kotlin.Float> = Float(rows, columns) { i, j ->
            var s = 0F
            for (k in 0 until columns) s += this[i, k] * that[k, j]
            s
        }
    }

    class Boolean(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        valueFunction: Matrix.ValueFunction.Independent<kotlin.Boolean>?,
    ) : LowLevelMatrix<kotlin.Boolean>() {
        override val structure = Structures.BooleanRing
        private val array = Array<BooleanArray>(rows) { BooleanArray(columns) }
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Boolean = array[i][j]
        override val transpose: Matrix<kotlin.Boolean> by lazy { Boolean(columns, rows) { i, j -> this[j, i] } }

        init {
            if (valueFunction != null)
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        array[i][j] = valueFunction(this, i, j)
        }

        override fun multiply(that: Matrix<kotlin.Boolean>): Matrix<kotlin.Boolean> = Boolean(rows, columns) { i, j ->
            var s = false
            for (k in 0 until columns) s = s xor (this[i, k] && that[k, j])
            s
        }
    }
}