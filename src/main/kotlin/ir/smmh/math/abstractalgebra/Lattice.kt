package ir.smmh.math.abstractalgebra

import ir.smmh.math.abstractalgebra.GroupLikeStructure.*
import ir.smmh.math.settheory.Set.PartiallyOrdered
import ir.smmh.math.MathematicalObject as M

/**
 * A [Lattice] is an [AlgebraicStructure] whose [domain] is a
 * [PartiallyOrdered] set, in which every pair of elements has a unique [join]
 * (also called a least upper bound or supremum) and a unique [meet] (also
 * called a greatest lower bound or infimum).
 *
 * Every lattice forms [SemiLattice]s over either of its operations, which are
 * respectively called its [joinGroup] and [meetGroup].
 *
 * In a lattice, meet and join are connected by the "absorption law".
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/Lattice_(order))
 */
interface Lattice<T : M> : AlgebraicStructure<T> {

    /*
     * TODO lattice examples
     * The power set of a set, partially ordered by inclusion, for which join
     * is the union and meet is the intersection. (distributive)
     * Natural numbers partially ordered by divisibility, for which join is the
     * least common multiple and meet is the greatest common divisor.
     */

    override val domain: PartiallyOrdered<T>

    /**
     * Join (⋁) gives the least upper bound, or supremum
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Join_and_meet)
     */
    fun join(a: T, b: T): T

    /**
     * Meet (⋀) gives the greatest lower bound, or infimum
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Join_and_meet)
     */
    fun meet(a: T, b: T): T

    val joinGroup: SemiLattice<T>
    val meetGroup: SemiLattice<T>

    /**
     * A [Bounded] lattice is a [Lattice] that has a [greatestElement] and a
     * [leastElement].
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Lattice_(order)#Bounded_lattice)
     */
    interface Bounded<T : M> : Lattice<T> {

        /**
         * Also called "minimum" or "bottom" element, and denoted by "0" or "⊥"
         */
        val leastElement: T

        /**
         * Also called "maximum" or "top" element, and denoted by "1" or "⊤"
         */
        val greatestElement: T

        override val joinGroup: UnitalSemiLattice<T>
        override val meetGroup: UnitalSemiLattice<T>
    }

    /**
     * A [Complete] lattice is a [Lattice], all subsets of whose [domain] have
     * both a join and a meet. Every non-empty finite domain lattice is
     * complete.
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Complete_lattice)
     */
    interface Complete<T : M> : Bounded<T> {
        override val domain: PartiallyOrdered.Finite<T>
    }

    /**
     * A [Complemented] lattice is a [Bounded] lattice in which every element
     * `a` has a [complement], i.e. an element `b` satisfying `a ∨ b = 1` and
     * `a ∧ b = 0`. Complements need not be unique.
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Complemented_lattice)
     */
    interface Complemented<T : M> : Bounded<T> {

        /**
         * `¬`
         */
        fun complement(a: T): T

        override val joinGroup: ComplementiveUnitalSemiLattice<T>
        override val meetGroup: ComplementiveUnitalSemiLattice<T>
    }

    /**
     * A [Distributive] lattice is a [Lattice] in which [join] and [meet]
     * distribute over each other.
     *
     * [Wikipedia](https://en.wikipedia.org/wiki/Distributive_lattice)
     */
    interface Distributive<T : M> : Lattice<T>

    /**
     * In a [Complemented] and [Distributive] lattice, complements are unique.
     */
    interface ComplementedAndDistributive<T : M> : Complemented<T>, Distributive<T>
}