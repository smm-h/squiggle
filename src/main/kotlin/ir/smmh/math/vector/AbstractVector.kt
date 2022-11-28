package ir.smmh.math.vector

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.matrix.AbstractMatrix
import ir.smmh.math.matrix.Matrix

abstract class AbstractVector<T> : AbstractMatrix<T>(), Vector<T> {
    override val transpose: Matrix<T> by lazy { Transpose.Immutable(this) }

    abstract class Mutable<T> : AbstractVector<T>(), Vector.Mutable<T> {
        override val transpose: Matrix.Mutable<T> by lazy { Transpose.Mutable(this) }
    }

    private abstract class Transpose<T> : Vector<T> {
        abstract val vector: Vector<T>
        override val rows: Int
            get() = 1
        override val columns: Int
            get() = vector.length
        override val length: Int
            get() = vector.length
        override val structure: RingLike<T>
            get() = vector.structure
        override val transpose: Matrix<T>
            get() = vector

        override fun get(i: Int, j: Int): T = vector[j]
        override fun get(i: Int): T = vector[i]

        class Immutable<T>(override val vector: Vector<T>) : Transpose<T>()
        class Mutable<T>(override val vector: Vector.Mutable<T>) : Transpose<T>(), Vector.Mutable<T> {

            override val transpose: Matrix.Mutable<T>
                get() = vector.transpose

            override fun set(i: Int, j: Int, value: T) =
                vector.set(j, value)

            override fun set(i: Int, value: T) =
                vector.set(i, value)
        }
    }
}