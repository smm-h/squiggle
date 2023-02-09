package ir.smmh.math.abstractalgebra

import ir.smmh.math.MathematicalObject as M

abstract class AbstractRingLikeStructure<T : M> : AlgebraicStructure.Abstract<T>(), RingLikeStructure<T> {

    abstract override fun add(a: T, b: T): T
    abstract override fun multiply(a: T, b: T): T

    override val additiveGroup: GroupLikeStructure<T> =
        object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure<T> {
            override val domain by this@AbstractRingLikeStructure::domain
            override fun operate(a: T, b: T) = add(a, b)
        }

    override val multiplicativeGroup: GroupLikeStructure<T> =
        object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure<T> {
            override val domain by this@AbstractRingLikeStructure::domain
            override fun operate(a: T, b: T) = multiply(a, b)
        }

    abstract class HasSubtraction<T : M> : AbstractRingLikeStructure<T>(),
        RingLikeStructure.SubtractionRing<T> {
        abstract val additiveIdentityElement: T
        override fun subtract(a: T, b: T): T = add(a, negate(b))

        override val additiveGroup: GroupLikeStructure.Group<T> =
            object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.Group<T> {
                override val domain by this@HasSubtraction::domain
                override fun operate(a: T, b: T) = add(a, b)
                override val identityElement by ::additiveIdentityElement
                override fun invert(a: T): T = negate(a)
            }
    }

    abstract class HasSubtractionAndDivision<T : M> : HasSubtraction<T>(),
        RingLikeStructure.DivisionRing<T> {
        abstract val multiplicativeIdentityElement: T
        override fun divide(a: T, b: T): T = multiply(a, reciprocal(b))

        override val additiveGroup: GroupLikeStructure.AbelianGroup<T> =
            object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.AbelianGroup<T> {
                override val domain by this@HasSubtractionAndDivision::domain
                override fun operate(a: T, b: T) = add(a, b)
                override val identityElement by ::additiveIdentityElement
                override fun invert(a: T): T = negate(a)
            }

        override val multiplicativeGroup: GroupLikeStructure.Group<T> =
            object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.Group<T> {
                override val domain by this@HasSubtractionAndDivision::domain
                override fun operate(a: T, b: T) = multiply(a, b)
                override val identityElement by ::multiplicativeIdentityElement
                override fun invert(a: T): T = reciprocal(a)
            }
    }
}