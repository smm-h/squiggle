package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.BuiltinNumberSet
import ir.smmh.math.numbers.Numbers
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.settheory.Set

object IntegerRing : RingLikeStructure.CommutativeRing<Numbers.Integer> {
    override val domain: Set<out Numbers.Integer> = BuiltinNumberSet.IntIntegers(1000)
    override val additiveGroup = object : GroupLikeStructure.AbelianGroup<Numbers.Integer> {
        override val domain by this@IntegerRing::domain
        override fun operate(a: Numbers.Integer, b: Numbers.Integer): Numbers.Integer = a + b
        override val identityElement: Numbers.Integer = ZERO
        override fun invert(a: Numbers.Integer): Numbers.Integer = -a
    }
    override val multiplicativeGroup = object : GroupLikeStructure.CommutativeMonoid<Numbers.Integer> {
        override val domain by this@IntegerRing::domain
        override fun operate(a: Numbers.Integer, b: Numbers.Integer): Numbers.Integer = a * b
        override val identityElement: Numbers.Integer = ONE
    }
}