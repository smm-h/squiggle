package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical

class StoredSet<T : MathematicalObject>(elements: Iterable<T>) : AbstractSet<T>(), Set.Finite<T> {
    constructor(vararg elements: T) : this(elements.asList())

    private val set = HashSet<T>().also { it.addAll(elements) }
    override val cardinality by set::size
    override val overElements: Iterable<T> = set
    override fun contains(it: T) = Logical.of(set.contains(it))
    override fun singletonOrNull() = set.firstOrNull()
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is StoredSet<*> && that.set == set) Logical.True else Knowable.Unknown
}