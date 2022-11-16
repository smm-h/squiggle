package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLike
import ir.smmh.nile.Mut

/**
 * The provided ring must have an additive identity
 */
class MapMatrix<T>(
    override val width: Int,
    override val height: Int,
    override val structure: RingLike<T>,
    override val mut: Mut = Mut(),
    val defaultValue: T = structure.addition.identity!!,
) : BaseMatrix<T>(), Matrix.Mutable<T> {
    private val map: MutableMap<Pair<Int, Int>, T> = HashMap()
    override fun get(i: Int, j: Int): T = map[i to j] ?: defaultValue
    override fun set(i: Int, j: Int, value: T) {
        map[i to j] = value
    }
}