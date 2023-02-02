package ir.smmh.math.matrix

import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.nile.Change
import ir.smmh.math.MathematicalObject as M

class ArrayVector<T : M>(
    override val length: Int,
    override val ring: RingLikeStructure.SubtractionRing<T>,
    override val changesToValues: Change
) : AbstractVector.Mutable<T>() {
    private val array = Array<Any>(length) {}

    @Suppress("UNCHECKED_CAST")
    override fun get(i: Int): T = array[i] as T

    override fun setWithoutMutation(i: Int, value: T) {
        array[i] = value as Any
    }
}