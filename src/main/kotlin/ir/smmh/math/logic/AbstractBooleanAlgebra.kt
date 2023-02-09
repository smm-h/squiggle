package ir.smmh.math.logic

import ir.smmh.math.abstractalgebra.AlgebraicStructure
import ir.smmh.math.abstractalgebra.GroupLikeStructure
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.MathematicalObject as M

abstract class AbstractBooleanAlgebra<T : M> : AlgebraicStructure.Abstract<T>(), BooleanAlgebra<T> {

    override abstract fun join(a: T, b: T): T
    override abstract fun meet(a: T, b: T): T

    override val joinGroup: GroupLikeStructure.ComplementiveUnitalSemiLattice<T> =
        object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.ComplementiveUnitalSemiLattice<T> {
            override val domain by this@AbstractBooleanAlgebra::domain
            override fun operate(a: T, b: T) = join(a, b)
            override val identityElement: T get() = leastElement
        }

    override val meetGroup: GroupLikeStructure.ComplementiveUnitalSemiLattice<T> =
        object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.ComplementiveUnitalSemiLattice<T> {
            override val domain by this@AbstractBooleanAlgebra::domain
            override fun operate(a: T, b: T) = meet(a, b)
            override val identityElement: T get() = greatestElement
        }

    override val asRing: RingLikeStructure.Ring<T> =
        object : AlgebraicStructure.Abstract<T>(), RingLikeStructure.Ring<T> {

            override val domain by this@AbstractBooleanAlgebra::domain

            override val additiveGroup: GroupLikeStructure.AbelianGroup<T> =
                object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.AbelianGroup<T> {
                    override val domain by this@AbstractBooleanAlgebra::domain
                    override fun operate(a: T, b: T) = symmetricDifference(a, b)
                    override val identityElement: T by ::greatestElement
                    override fun invert(a: T) = complement(a)
                }

            override val multiplicativeGroup: GroupLikeStructure.Monoid<T> =
                object : AlgebraicStructure.Abstract<T>(), GroupLikeStructure.Monoid<T> {
                    override val domain by this@AbstractBooleanAlgebra::domain
                    override fun operate(a: T, b: T) = meet(a, b)
                    override val identityElement: T by ::greatestElement
                }
        }
}