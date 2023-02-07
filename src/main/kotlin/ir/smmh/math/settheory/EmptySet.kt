package ir.smmh.math.settheory

import ir.smmh.math.logic.Logical
import ir.smmh.math.MathematicalObject as M

class EmptySet<T : M> : Set.Finite<T> {
    override fun contains(it: T) = Logical.False
    override val cardinality: Int get() = 0
    override val overElements: Iterable<T> = emptySet()
    override val debugText: String = "EmptySet"
    override val tex: String = "\\emptyset"
    override fun isNonReferentiallyEqualTo(that: M) = if (that is Set<*>) that.isEmpty() else Logical.False
    override fun singletonOrNull() = null
}