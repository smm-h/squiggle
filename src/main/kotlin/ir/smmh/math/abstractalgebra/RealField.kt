package ir.smmh.math.abstractalgebra

import ir.smmh.math.numbers.Numbers.ONE
import ir.smmh.math.numbers.Numbers.Real
import ir.smmh.math.numbers.Numbers.ZERO
import ir.smmh.math.numbers.UniversalNumberSet
import ir.smmh.math.settheory.Set

object RealField : AlgebraicStructure.Abstract<Real>(), RingLikeStructure.Field<Real> {
    override val domain: Set<Real> = UniversalNumberSet.R(1000)
    override val additiveGroup: GroupLikeStructure.AbelianGroup<Real> =
        object : AlgebraicStructure.Abstract<Real>(), GroupLikeStructure.AbelianGroup<Real> {
            override val domain by this@RealField::domain
            override fun operate(a: Real, b: Real): Real = a + b
            override val identityElement: Real = ZERO
            override fun invert(a: Real): Real = -a
        }
    override val multiplicativeGroup: GroupLikeStructure.AbelianGroup<Real> =
        object : AlgebraicStructure.Abstract<Real>(), GroupLikeStructure.AbelianGroup<Real> {
            override val domain by this@RealField::domain
            override fun operate(a: Real, b: Real): Real = a * b
            override val identityElement: Real = ONE
            override fun invert(a: Real): Real = a.reciprocal
        }
}