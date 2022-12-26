package ir.smmh.math.settheory

import kotlin.random.Random

class StoredSet<T : Any>(preFillElements: Iterable<T>? = null) : AbstractSet(), Set.Specific.Finite<T> {
    private val set = HashSet<T>().apply { preFillElements?.also { addAll(it) } }
    private val list by lazy { set.toList() }
    override val cardinality by set::size
    override val choose: () -> T = { list[Random.nextInt(cardinality)] }
    override fun containsSpecific(it: T): Boolean = it in set
    override val overElements: Iterable<T> = set
    override fun equals(other: Any?) = other is StoredSet<*> && other.set == set
    override fun hashCode() = set.hashCode()
    override fun toString() = set.toString()
    override fun singletonNullable() = if (cardinality == 1) set.firstOrNull() else null
}