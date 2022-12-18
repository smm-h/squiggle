package ir.smmh.math.settheory

import ir.smmh.nile.Cache
import kotlin.random.Random

class FiniteIntegersSet private constructor(override val cardinality: Int) : AbstractSet(), Set.Specific.Finite<Int> {
    override val choose: () -> Int = { Random.nextInt(cardinality) }
    override fun containsSpecific(it: Int): Boolean = it >= 0 && it < cardinality
    override val over: Iterable<Int> by lazy { 0 until cardinality }
    override fun singletonNullable() = if (cardinality == 1) 0 else null
    companion object {
        private val cache = Cache<Int, FiniteIntegersSet> { FiniteIntegersSet(it) }
        fun of(degree: Int): FiniteIntegersSet = cache(degree)
    }
}