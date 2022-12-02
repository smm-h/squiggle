package ir.smmh.math.settheory

import ir.smmh.math.symbolic.TeXable
import kotlin.random.Random

class StoredSet<T : Any>(val elements: Iterable<T> = emptySet()) : Set.Specific.Finite<T>, TeXable {
    private val set = elements.toSet()
    private val list = set.toList()
    override val cardinality: Int by list::size
    override val choose: () -> T = { list[Random.nextInt(cardinality)] }
    override fun containsSpecific(it: T): Boolean = it in set
    override val over: Iterable<T> = list
    override val tex: String by lazy { list.joinToString(", ", "\\{", "\\}", transform = TeXable::texOf) }
}