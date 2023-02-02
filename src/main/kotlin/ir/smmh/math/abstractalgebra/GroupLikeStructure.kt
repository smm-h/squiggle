package ir.smmh.math.abstractalgebra

import ir.smmh.math.MathematicalObject as M


interface GroupLikeStructure<T : M> : AlgebraicStructure<T> {
    fun operate(a: T, b: T): T

    interface Closed<T : M> : GroupLikeStructure<T>
    interface Associative<T : M> : GroupLikeStructure<T>
    interface Commutative<T : M> : GroupLikeStructure<T>
    interface Divisible<T : M> : GroupLikeStructure<T>
    interface Regular<T : M> : LeftRegular<T>, RightRegular<T>
    interface LeftRegular<T : M> : GroupLikeStructure<T>
    interface RightRegular<T : M> : GroupLikeStructure<T>
    interface Idempotent<T : M> : GroupLikeStructure<T>
    interface Cancellative<T : M> : LeftCancellative<T>, RightCancellative<T>
    interface LeftCancellative<T : M> : GroupLikeStructure<T>
    interface RightCancellative<T : M> : GroupLikeStructure<T>
    interface Complementive<T : M> : GroupLikeStructure<T>
    interface Monotonic<T : M> : GroupLikeStructure<T>

    interface Unital<T : M> : GroupLikeStructure<T> {
        val identityElement: T
    }

    interface Invertible<T : M> : Unital<T>, Divisible<T> {
        fun invert(a: T): T
        fun operateInverse(a: T, b: T): T = operate(a, invert(b))
    }

    interface Magma<T : M> : Closed<T>
    interface UnitalMagma<T : M> : Unital<T>, Magma<T>
    interface CommutativeMagma<T : M> : Commutative<T>, Magma<T>
    interface Loop<T : M> : Invertible<T>, Magma<T>
    interface Group<T : M> : Invertible<T>, Monoid<T>
    interface AbelianGroup<T : M> : CommutativeMonoid<T>, Group<T>
    interface QuasiGroup<T : M> : Divisible<T>, Magma<T>
    interface Groupoid<T : M> : Divisible<T>, SmallCategory<T>
    interface SemiGroupoid<T : M> : Associative<T>
    interface SmallCategory<T : M> : Unital<T>, SemiGroupoid<T>
    interface SemiGroup<T : M> : SemiGroupoid<T>, Magma<T>
    interface RegularSemiGroup<T : M> : Regular<T>, SemiGroup<T>
    interface InverseSemiGroup<T : M> : Invertible<T>, SemiGroup<T>
    interface Monoid<T : M> : SemiGroup<T>, UnitalMagma<T>
    interface CommutativeMonoid<T : M> : CommutativeMagma<T>, Monoid<T>
    interface Band<T : M> : Idempotent<T>, SemiGroup<T>
    interface SemiLattice<T : M> : CommutativeMagma<T>, Band<T>
    interface UnitalSemiLattice<T : M> : CommutativeMonoid<T>, SemiLattice<T>
    interface ComplementiveUnitalSemiLattice<T : M> : Complementive<T>, UnitalSemiLattice<T>
    interface InverseComplementiveUnitalSemiLattice<T : M> : Group<T>, ComplementiveUnitalSemiLattice<T>
    interface ComplementiveAbelianGroup<T : M> : Complementive<T>, AbelianGroup<T>
}

