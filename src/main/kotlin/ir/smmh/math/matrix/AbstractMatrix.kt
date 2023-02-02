package ir.smmh.math.matrix

import ir.smmh.math.MathematicalObject

abstract class AbstractMatrix<T : MathematicalObject> : MathematicalObject.Abstract(), Matrix<T> {
    override val debugText: String by lazy {
        StringBuilder().apply {
            val n = rows - 1
            for (i in 0 until rows) {
                if (i != 0) append('\n')
                append(
                    when (i) {
                        0 -> '┌'
                        n -> '└'
                        else -> '│'
                    }
                )
                append('\t')
                for (j in 0 until columns) {
                    if (j != 0) append('\t')
                    append(get(i, j))
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

    abstract class Mutable<T : MathematicalObject> : AbstractMatrix<T>(), Matrix.Mutable<T> {
        override val transpose: Matrix.Mutable<T> get() = createSameStructure(columns, rows).setTransposed(this)
        abstract fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T>
    }
}