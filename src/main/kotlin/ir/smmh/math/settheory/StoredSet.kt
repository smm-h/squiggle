package ir.smmh.math.settheory

import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import ir.smmh.math.MathematicalObject as M

class StoredSet<T : M>(elements: Iterable<T>) : AbstractSet<T>(), Set.Finite<T> {
    constructor(vararg elements: T) : this(elements.asList())

    private val set = HashSet<T>().also { it.addAll(elements) }
    override val cardinality by set::size
    override val overElements: Iterable<T> = set
    override fun contains(it: T) = Logical.of(set.contains(it))
    override fun singletonOrNull() = set.firstOrNull()
    override fun isNonReferentiallyEqualTo(that: M): Knowable =
        if (that is StoredSet<*> && that.set == set) Logical.True else Knowable.Unknown
}