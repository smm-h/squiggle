package ir.smmh.math.abstractalgebra

import ir.smmh.math.MathematicalObject as M

interface RingLikeStructure<T : M> : AlgebraicStructure<T> {

    val additiveGroup: GroupLikeStructure<T>
    val multiplicativeGroup: GroupLikeStructure<T>

    fun add(a: T, b: T): T = additiveGroup.operate(a, b)
    fun multiply(a: T, b: T): T = multiplicativeGroup.operate(a, b)

    interface Distributive<T : M> : LeftDistributive<T>, RightDistributive<T>,
        AdditivelyDistributive<T>, MultiplicativelyDistributive<T>

    interface LeftDistributive<T : M> : AdditivelyLeftDistributive<T>,
        MultiplicativelyLeftDistributive<T>

    interface RightDistributive<T : M> : AdditivelyRightDistributive<T>,
        MultiplicativelyRightDistributive<T>

    interface AdditivelyDistributive<T : M> : AdditivelyLeftDistributive<T>,
        AdditivelyRightDistributive<T>

    interface AdditivelyLeftDistributive<T : M> : RingLikeStructure<T>
    interface AdditivelyRightDistributive<T : M> : RingLikeStructure<T>

    interface MultiplicativelyDistributive<T : M> : MultiplicativelyLeftDistributive<T>,
        MultiplicativelyRightDistributive<T>

    interface MultiplicativelyLeftDistributive<T : M> : RingLikeStructure<T>
    interface MultiplicativelyRightDistributive<T : M> : RingLikeStructure<T>

    interface SubtractionRing<T : M> : RingLikeStructure<T> {
        override val additiveGroup: GroupLikeStructure.Group<T>
        fun negate(a: T): T = additiveGroup.invert(a)
        fun subtract(a: T, b: T): T = additiveGroup.operateInverse(a, b)
    }

    interface Rng<T : M> : SubtractionRing<T> {
        override val additiveGroup: GroupLikeStructure.AbelianGroup<T>
        override val multiplicativeGroup: GroupLikeStructure.SemiGroup<T>
    }

    // TODO Lie algebra
    interface NonAssociativeRing<T : M> : RingLikeStructure<T> {
        override val additiveGroup: GroupLikeStructure.SemiGroup<T>
        override val multiplicativeGroup: GroupLikeStructure.Magma<T>
    }

    // TODO Natural numbers
    interface SemiRing<T : M> : RingLikeStructure<T> {
        override val additiveGroup: GroupLikeStructure.CommutativeMonoid<T>
        override val multiplicativeGroup: GroupLikeStructure.Monoid<T>
    }

    interface Ring<T : M> : Rng<T>, NonAssociativeRing<T>, SemiRing<T> {
        override val additiveGroup: GroupLikeStructure.AbelianGroup<T>
        override val multiplicativeGroup: GroupLikeStructure.Monoid<T>
    }

    interface CommutativeRing<T : M> : Ring<T> {
        override val multiplicativeGroup: GroupLikeStructure.CommutativeMonoid<T>
    }

    interface DivisionRing<T : M> : Ring<T> {
        override val multiplicativeGroup: GroupLikeStructure.Group<T>
        fun reciprocal(a: T): T = multiplicativeGroup.invert(a)
        fun divide(a: T, b: T): T = multiplicativeGroup.operateInverse(a, b)
    }

    interface Field<T : M> : CommutativeRing<T>, DivisionRing<T> {
        override val multiplicativeGroup: GroupLikeStructure.AbelianGroup<T>
    }

    /**
     * [Wikipedia](https://en.wikipedia.org/wiki/Integral_domain)
     */
    interface IntegralDomain<T : M> : CommutativeRing<T> {
        // TODO Integers

    }

    // https://en.wikipedia.org/wiki/Domain_(ring_theory)

    interface NearRing<T : M> : LeftNearRing<T>, RightNearRing<T>, MultiplicativelyDistributive<T>
    interface LeftNearRing<T : M> : BaseNearRing<T>, MultiplicativelyLeftDistributive<T>
    interface RightNearRing<T : M> : BaseNearRing<T>, MultiplicativelyRightDistributive<T>
    interface BaseNearRing<T : M> : RingLikeStructure<T> {
        override val additiveGroup: GroupLikeStructure.Group<T>
        override val multiplicativeGroup: GroupLikeStructure.SemiGroup<T>
    }
}