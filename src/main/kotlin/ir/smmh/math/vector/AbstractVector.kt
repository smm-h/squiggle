package ir.smmh.math.vector

import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.matrix.AbstractMatrix
import ir.smmh.math.matrix.Matrix
import ir.smmh.nile.Change
import ir.smmh.math.MathematicalObject as M

abstract class AbstractVector<T : M> : AbstractMatrix<T>(), Vector<T> {
    override val transpose: Matrix<T> by lazy { Transpose.Immutable(this) }

    abstract class Mutable<T : M> : AbstractVector<T>(), Vector.Mutable<T> {
        override val transpose: Matrix.Mutable<T> by lazy { Transpose.Mutable(this) }
    }

    private abstract class Transpose<T : M> : Vector<T> {
        abstract val vector: Vector<T>
        override val rows: Int
            get() = 1
        override val columns: Int
            get() = vector.length
        override val length: Int
            get() = vector.length
        override val ring: RingLikeStructure<T>
            get() = vector.ring
        override val transpose: Matrix<T>
            get() = vector

        override fun get(i: Int, j: Int): T = vector[j]
        override fun get(i: Int): T = vector[i]

        class Immutable<T : M>(
            override val vector: Vector<T>,
        ) : Transpose<T>()

        class Mutable<T : M>(
            override val vector: Vector.Mutable<T>,
            override val changesToValues: Change = Change(),
        ) : Transpose<T>(), Vector.Mutable<T> {

            override val transpose: Matrix.Mutable<T>
                get() = vector.transpose

            override fun set(i: Int, j: Int, value: T) =
                vector.set(j, value)

            override fun set(i: Int, value: T) =
                vector.set(i, value)
        }
    }
}