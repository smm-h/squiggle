package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.nile.Mut

/**
 * The provided ring must have an additive identity
 */
class MapMatrix<T>(
    override val rows: Int,
    override val columns: Int,
    override val structure: RingLike<T>,
//    override val mut: Mut = Mut(),
    val defaultValue: T = structure.addition.identity!!,
) : AbstractMatrix<T>(), Matrix.Mutable<T> {

    private val map: MutableMap<Int, T> = HashMap()

    override fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T> = MapMatrix(rows, columns, structure)
    override fun get(i: Int, j: Int): T = map[pair(i, j)] ?: defaultValue
    override fun set(i: Int, j: Int, value: T) {
//        mut.preMutate()
        map[pair(i, j)] = value
//        mut.mutate()
    }
}