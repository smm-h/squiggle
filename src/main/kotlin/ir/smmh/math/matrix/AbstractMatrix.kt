package ir.smmh.math.matrix

abstract class AbstractMatrix<T> internal constructor() : Matrix<T> {

    override fun toString(): String =
        Matrix.toString(this)

    override fun hashCode(): Int =
        Matrix.hashCode(this)

    override fun equals(other: Any?): Boolean =
        if (other is Matrix<*>) areEqual(other) else super.equals(other)
}