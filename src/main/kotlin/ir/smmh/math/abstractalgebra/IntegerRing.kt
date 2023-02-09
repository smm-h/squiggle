package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.Numbers.Integer
import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.numbers.UniversalNumberSet
import ir.smmh.math.settheory.Set

object IntegerRing : AlgebraicStructure.Abstract<Integer>(), RingLikeStructure.CommutativeRing<Integer> {
    override val domain: Set<Integer> = UniversalNumberSet.Z(1000)
    override val additiveGroup: GroupLikeStructure.AbelianGroup<Integer> =
        object : AlgebraicStructure.Abstract<Integer>(), GroupLikeStructure.AbelianGroup<Integer> {
            override val domain by this@IntegerRing::domain
            override fun operate(a: Integer, b: Integer): Integer = a + b
            override val identityElement: Integer = ZERO
            override fun invert(a: Integer): Integer = -a
        }
    override val multiplicativeGroup: GroupLikeStructure.CommutativeMonoid<Integer> =
        object : AlgebraicStructure.Abstract<Integer>(), GroupLikeStructure.CommutativeMonoid<Integer> {
            override val domain by this@IntegerRing::domain
            override fun operate(a: Integer, b: Integer): Integer = a * b
            override val identityElement: Integer = ONE
        }
}