package ir.smmh.math.settheory

import ir.smmh.nile.Sequential
import kotlin.random.Random

class StoredSet<T : Any>(val elements: Sequential<T>) : Set.Specific.Finite<T> {
    override val cardinality: Int get() = elements.size
    override val choose: () -> T = { elements.getAtIndex(Random.nextInt(cardinality)) }
    override fun containsSpecific(it: T): Boolean = it in elements
}