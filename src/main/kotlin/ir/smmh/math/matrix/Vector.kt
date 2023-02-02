package ir.smmh.math.matrix

import ir.smmh.math.MathematicalObject as M

interface Vector<T : M> : Matrix<T> {
    val length: Int
    override val rows: Int get() = length
    override val columns: Int get() = 1
    operator fun get(i: Int): T
    override fun get(i: Int, j: Int): T = get(i)

    interface Mutable<T : M> : Vector<T>, Matrix.Mutable<T> {
        fun setWithoutMutation(i: Int, value: T)

        operator fun set(i: Int, value: T) {
            changesToValues.beforeChange()
            setWithoutMutation(i, value)
            changesToValues.afterChange()
        }

        override fun setWithoutMutation(i: Int, j: Int, value: T) = setWithoutMutation(i, value)
    }
}


