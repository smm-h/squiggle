package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.Structures
import ir.smmh.nile.Mut

sealed class LowLevelMatrix<T> : AbstractMatrix<T>(), Matrix.Mutable<T> {

    class Int(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        override val mut: Mut = Mut(),
    ) : LowLevelMatrix<kotlin.Int>() {
        override val structure = Structures.Integer32Ring
        override fun createSameStructure(rows: kotlin.Int, columns: kotlin.Int) = Int(rows, columns)
        private val twod = Array2D.Int(rows, columns)
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Int = twod.array[i][j]
        override fun set(i: kotlin.Int, j: kotlin.Int, value: kotlin.Int) {
            mut.preMutate()
            twod.array[i][j] = value
            mut.mutate()
        }
    }

    class Long(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        override val mut: Mut = Mut(),
    ) : LowLevelMatrix<kotlin.Long>() {
        override val structure = Structures.Integer64Ring
        override fun createSameStructure(rows: kotlin.Int, columns: kotlin.Int) = Long(rows, columns)
        private val twod = Array2D.Long(rows, columns)
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Long = twod.array[i][j]
        override fun set(i: kotlin.Int, j: kotlin.Int, value: kotlin.Long) {
            mut.preMutate()
            twod.array[i][j] = value
            mut.mutate()
        }
    }

    class Double(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        override val mut: Mut = Mut(),
    ) : LowLevelMatrix<kotlin.Double>() {
        override val structure = Structures.RealDPField
        override fun createSameStructure(rows: kotlin.Int, columns: kotlin.Int) = Double(rows, columns)
        private val twod = Array2D.Double(rows, columns)
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Double = twod.array[i][j]
        override fun set(i: kotlin.Int, j: kotlin.Int, value: kotlin.Double) {
            mut.preMutate()
            twod.array[i][j] = value
            mut.mutate()
        }
    }

    class Float(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        override val mut: Mut = Mut(),
    ) : LowLevelMatrix<kotlin.Float>() {
        override val structure = Structures.RealFPField
        override fun createSameStructure(rows: kotlin.Int, columns: kotlin.Int) = Float(rows, columns)
        private val twod = Array2D.Float(rows, columns)
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Float = twod.array[i][j]
        override fun set(i: kotlin.Int, j: kotlin.Int, value: kotlin.Float) {
            mut.preMutate()
            twod.array[i][j] = value
            mut.mutate()
        }
    }

    class Boolean(
        override val rows: kotlin.Int,
        override val columns: kotlin.Int,
        override val mut: Mut = Mut(),
    ) : LowLevelMatrix<kotlin.Boolean>() {
        override val structure = Structures.BooleanRing
        override fun createSameStructure(rows: kotlin.Int, columns: kotlin.Int) = Boolean(rows, columns)
        private val twod = Array2D.Boolean(rows, columns)
        override fun get(i: kotlin.Int, j: kotlin.Int): kotlin.Boolean = twod.array[i][j]
        override fun set(i: kotlin.Int, j: kotlin.Int, value: kotlin.Boolean) {
            mut.preMutate()
            twod.array[i][j] = value
            mut.mutate()
        }
    }
}