package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.BuiltinNumberSet
import ir.smmh.math.numbers.Numbers
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.settheory.Set

object RealField : RingLikeStructure.Field<Numbers.Real> {
    override val domain: Set<Numbers.Real> = BuiltinNumberSet.DoubleReals(1000.0)
    override val additiveGroup = object : GroupLikeStructure.AbelianGroup<Numbers.Real> {
        override val domain by this@RealField::domain
        override fun operate(a: Numbers.Real, b: Numbers.Real): Numbers.Real = a + b
        override val identityElement: Numbers.Real = ZERO
        override fun invert(a: Numbers.Real): Numbers.Real = -a
    }
    override val multiplicativeGroup = object : GroupLikeStructure.AbelianGroup<Numbers.Real> {
        override val domain by this@RealField::domain
        override fun operate(a: Numbers.Real, b: Numbers.Real): Numbers.Real = a * b
        override val identityElement: Numbers.Real = ONE
        override fun invert(a: Numbers.Real): Numbers.Real = a.reciprocal
    }
}