package ir.smmh.math.matrix

import ir.smmh.math.MathematicalObject as M

abstract class AbstractMatrix<T : M> : M.Abstract(), Matrix<T> {
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

    // TODO requiresLatex
    override val tex by lazy {
        (0 until rows).joinToString(" \\\\\n", "{\\begin{bmatrix}\n", "\n\\end{bmatrix}}")
        { i -> row(i).overValues.joinToString(" & ") { it.tex } }
    }

    abstract class Mutable<T : M> : AbstractMatrix<T>(), Matrix.Mutable<T> {
        override val transpose: Matrix.Mutable<T> get() = createSameStructure(columns, rows).setTransposed(this)
        abstract fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T>
    }
}