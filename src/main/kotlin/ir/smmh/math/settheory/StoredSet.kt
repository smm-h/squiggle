package ir.smmh.math.settheory

import kotlin.random.Random

class StoredSet<T : Any>(val elements: Iterable<T> = emptySet()) : AbstractSet(), Set.Specific.Finite<T> {
    private val set = elements.toSet()
    private val list = set.toList()
    override val cardinality by list::size
    override val choose: () -> T = { list[Random.nextInt(cardinality)] }
    override fun containsSpecific(it: T): Boolean = it in set
    override val over: Iterable<T> = list
    override fun hashCode() = elements.hashCode()
    // TODO equals
    override fun singletonNullable() = if (cardinality == 1) list[0] else null
}