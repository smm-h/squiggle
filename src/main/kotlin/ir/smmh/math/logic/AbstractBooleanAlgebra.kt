package ir.smmh.math.logic

import ir.smmh.math.abstractalgebra.GroupLikeStructure
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.settheory.Set
import ir.smmh.math.MathematicalObject as M

abstract class AbstractBooleanAlgebra<T : M> : BooleanAlgebra<T> {

    override abstract fun join(a: T, b: T): T
    override abstract fun meet(a: T, b: T): T

    override val joinGroup = object : GroupLikeStructure.ComplementiveUnitalSemiLattice<T> {
        override val domain by this@AbstractBooleanAlgebra::domain
        override fun operate(a: T, b: T) = join(a, b)
        override val identityElement: T get() = leastElement
    }

    override val meetGroup = object : GroupLikeStructure.ComplementiveUnitalSemiLattice<T> {
        override val domain by this@AbstractBooleanAlgebra::domain
        override fun operate(a: T, b: T) = meet(a, b)
        override val identityElement: T get() = greatestElement
    }

    override val asRing = object : RingLikeStructure.Ring<T> {

        override val domain by this@AbstractBooleanAlgebra::domain

        override val additiveGroup = object : GroupLikeStructure.AbelianGroup<T> {
            override val domain by this@AbstractBooleanAlgebra::domain
            override fun operate(a: T, b: T) = symmetricDifference(a, b)
            override val identityElement: T by ::greatestElement
            override fun invert(a: T) = complement(a)
        }

        override val multiplicativeGroup = object : GroupLikeStructure.Monoid<T> {
            override val domain by this@AbstractBooleanAlgebra::domain
            override fun operate(a: T, b: T) = meet(a, b)
            override val identityElement: T by ::greatestElement
        }
    }
}