package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.nile.Change

/**
 * The provided ring must have an additive identity
 */
class MapMatrix<T : Any>(
    override val rows: Int,
    override val columns: Int,
    override val structure: RingLike<T>,
    override val changesToValues: Change = Change(),
    val defaultValue: T = structure.addition.identity!!,
) : AbstractMatrix.Mutable<T>() {

    private val map: MutableMap<Int, T> = HashMap()

    override fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T> = MapMatrix(rows, columns, structure)
    override fun get(i: Int, j: Int): T = map[pair(i, j)] ?: defaultValue
    override fun set(i: Int, j: Int, value: T) {
//        change.preMutate()
        map[pair(i, j)] = value
//        change.mutate()
    }
}