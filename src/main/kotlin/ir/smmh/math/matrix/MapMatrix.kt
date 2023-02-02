package ir.smmh.math.matrix

import ir.smmh.math.MathematicalObject
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.nile.Change

/**
 * The provided ring must have an additive identity
 */
class MapMatrix<T : MathematicalObject>(
    override val rows: Int,
    override val columns: Int,
    override val ring: RingLikeStructure.SubtractionRing<T>,
    override val changesToValues: Change = Change(),
    val defaultValue: T = ring.additiveGroup.identityElement,
) : AbstractMatrix.Mutable<T>() {

    private val map: MutableMap<Int, T> = HashMap()

    override fun createSameStructure(rows: Int, columns: Int): Matrix.Mutable<T> = MapMatrix(rows, columns, ring)

    override fun get(i: Int, j: Int): T = map[pair(i, j)] ?: defaultValue

    override fun setWithoutMutation(i: Int, j: Int, value: T) {
        map[pair(i, j)] = value
    }
}