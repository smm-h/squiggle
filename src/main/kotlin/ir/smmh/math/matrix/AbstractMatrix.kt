package ir.smmh.math.matrix

abstract class AbstractMatrix<T> internal constructor() : Matrix<T> {

    override fun toString(): String =
        Matrix.toString(this)

    override fun hashCode(): Int =
        Matrix.hashCode(this)

    override fun equals(other: Any?): Boolean =
        if (other is Matrix<*>) areEqual(other) else super.equals(other)

    abstract class Mutable<T> : AbstractMatrix<T>(), Matrix.Mutable<T> {
        override val transpose: Matrix.Mutable<T> get() = createSameStructure(columns, rows).setTransposed(this)

        abstract fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T>
    }
}