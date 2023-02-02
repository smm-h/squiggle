package ir.smmh.math.logic

import ir.smmh.math.abstractalgebra.Lattice
import ir.smmh.math.abstractalgebra.RingLikeStructure
import ir.smmh.math.MathematicalObject as M

/**
 * A [BooleanAlgebra] is any complemented and distributive [Lattice].
 */
interface BooleanAlgebra<T : M> : Lattice.Complete<T>, Lattice.ComplementedAndDistributive<T> {

    /**
     * `+`
     *
     * `a + b := (a ∧ ¬b) ∨ (b ∧ ¬a) = (a ∨ b) ∧ ¬(a ∧ b)`
     *
     * This operation is called "xor" in logic.
     */
    fun symmetricDifference(a: T, b: T): T = join(meet(a, complement(b)), meet(b, complement(a)))

    val asRing: RingLikeStructure.Ring<T>
}