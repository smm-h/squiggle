package ir.smmh.math.matrix

import ir.smmh.math.MathematicalObject
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.matrix.Matrix.ValueFunction.constant
import ir.smmh.nile.Change

class ArrayMatrix<T : MathematicalObject>(
    override val rows: Int,
    override val columns: Int,
    override val ring: RingLikeStructure.SubtractionRing<T>,
    override val changesToValues: Change = Change(),
    initialValueFunction: (Int, Int) -> T,
) : AbstractMatrix.Mutable<T>() {
    constructor(
        rows: Int,
        columns: Int,
        structure: RingLikeStructure.SubtractionRing<T>,
        changesToValues: Change = Change(),
        initialValue: T = structure.additiveGroup.identityElement,
    ) : this(rows, columns, structure, changesToValues, constant(initialValue))

    private val array = Array<Array<Any>>(rows) { i -> Array<Any>(columns) { j -> initialValueFunction(i, j) } }

    override fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T> = ArrayMatrix(rows, columns, ring)

    @Suppress("UNCHECKED_CAST")
    override fun get(i: Int, j: Int): T = array[i][j] as T

    override fun setWithoutMutation(i: Int, j: Int, value: T) {
        array[i][j] = value
    }
}