package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.nile.Mut

class ArrayMatrix<T : Any>(
    override val rows: Int,
    override val columns: Int,
    override val structure: RingLike<T>,
    override val mut: Mut = Mut(),
    initialValueFunction: Matrix.ValueFunction<T>,
) : AbstractMatrix<T>(), Matrix.Mutable<T> {
    constructor(
        rows: Int,
        columns: Int,
        structure: RingLike<T>,
        mut: Mut = Mut(),
        initialValue: T = structure.addition.identity!!,
    ) : this(rows, columns, structure, mut, Matrix.ValueFunction.Independent { _, _ -> initialValue })

    private val array = Array<Array<Any>>(rows, { i -> Array<Any>(columns, { j -> initialValueFunction(this, i, j) }) })

    override fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T> = ArrayMatrix(rows, columns, structure)

    @Suppress("UNCHECKED_CAST")
    override fun get(i: Int, j: Int): T = array[i][j] as T
    override fun set(i: Int, j: Int, value: T) {
        mut.preMutate()
        array[i][j] = value
        mut.mutate()
    }
}