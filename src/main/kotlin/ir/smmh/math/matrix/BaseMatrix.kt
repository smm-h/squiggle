package ir.smmh.math.matrix

abstract class BaseMatrix<T> internal constructor() : Matrix<T> {

    override fun equals(other: Any?): Boolean {
        if (other is Matrix<*>) {
            if (rows == other.rows && columns == other.columns && structure == other.structure) {
                for (i in 0 until rows)
                    for (j in 0 until columns)
                        if (this[i, j] != other[i, j])
                            return false
                return true
            } else return false
        } else return super.equals(other)
    }

    override fun toString(): String = StringBuilder().apply {
        for (i in 0 until rows) {
            if (i != 0) append('\n')
            append(
                when (i) {
                    0 -> '┌'
                    rows - 1 -> '└'
                    else -> '│'
                }
            )
            append('\t')
            for (j in 0 until columns) {
                if (j != 0) append('\t')
                append(this@BaseMatrix[i, j])
            }
            append('\t')
            append(
                when (i) {
                    0 -> '┐'
                    rows - 1 -> '┘'
                    else -> '│'
                }
            )
        }
    }.toString()
}