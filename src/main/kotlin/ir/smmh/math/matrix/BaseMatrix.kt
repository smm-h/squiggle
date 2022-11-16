package ir.smmh.math.matrix

abstract class BaseMatrix<T> internal constructor() : Matrix<T> {

    override fun equals(other: Any?): Boolean {
        if (other is Matrix<*>) {
            if (width == other.width && height == other.height && structure == other.structure) {
                for (i in 0 until width)
                    for (j in 0 until height)
                        if (this[i, j] != other[i, j])
                            return false
                return true
            } else return false
        } else return super.equals(other)
    }

    override fun toString(): String = StringBuilder().apply {
        for (i in 0 until width) {
            if (i != 0) append('\n')
            append(
                when (i) {
                    0 -> '┌'
                    width - 1 -> '└'
                    else -> '│'
                }
            )
            append('\t')
            for (j in 0 until height) {
                if (j != 0) append('\t')
                append(this@BaseMatrix[i, j])
            }
            append('\t')
            append(
                when (i) {
                    0 -> '┐'
                    width - 1 -> '┘'
                    else -> '│'
                }
            )
        }
    }.toString()
}