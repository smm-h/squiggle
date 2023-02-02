package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.Numbers.Natural
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.settheory.Set
import ir.smmh.math.settheory.Singleton

/**
 * [Wikipedia](https://en.wikipedia.org/wiki/Zero_ring)
 */
object ZeroRing : AbstractRingLikeStructure.HasSubtraction<Natural>() {
    override val domain: Set<Natural> = Singleton.of(ZERO)
    override fun add(a: Natural, b: Natural) = ZERO
    override fun multiply(a: Natural, b: Natural) = ZERO
    override val additiveIdentityElement = ZERO
    override fun negate(a: Natural): Natural = ZERO
}