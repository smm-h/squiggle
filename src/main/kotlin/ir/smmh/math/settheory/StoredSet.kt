package ir.smmh.math.settheory

import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable

class StoredSet<T : MathematicalObject>(elements: Iterable<T>) : AbstractSet<T>(), Set.Finite.KnownCardinality<T> {
    private val set = HashSet<T>().also { it.addAll(elements) }
    override val cardinality by set::size
    override val overElements: Iterable<T> = set
    override fun contains(it: T): Boolean = set.contains(it)
    override fun singletonOrNull() = set.firstOrNull()
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is StoredSet<*> && that.set == set) Knowable.Known.True else Knowable.Unknown
}