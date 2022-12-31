package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.math.matrix.Matrix.ValueFunction.constant
import ir.smmh.nile.Change

class ArrayMatrix<T : Any>(
    override val rows: Int,
    override val columns: Int,
    override val structure: RingLike<T>,
    override val changesToValues: Change = Change(),
    initialValueFunction: (Int, Int) -> T,
) : AbstractMatrix.Mutable<T>() {
    constructor(
        rows: Int,
        columns: Int,
        structure: RingLike<T>,
        changesToValues: Change = Change(),
        initialValue: T = structure.addition.identity!!,
    ) : this(rows, columns, structure, changesToValues, constant(initialValue))

    private val array = Array<Array<Any>>(rows) { i -> Array<Any>(columns) { j -> initialValueFunction(i, j) } }

    override fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T> = ArrayMatrix(rows, columns, structure)

    @Suppress("UNCHECKED_CAST")
    override fun get(i: Int, j: Int): T = array[i][j] as T
    override fun set(i: Int, j: Int, value: T) {
        changesToValues.beforeChange()
        array[i][j] = value
        changesToValues.afterChange()
    }
}