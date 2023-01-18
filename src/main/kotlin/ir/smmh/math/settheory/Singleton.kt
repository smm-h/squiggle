package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import kotlin.random.Random

class Singleton<T : MathematicalObject>(val value: T) : AbstractSet<T>(), Set.Finite<T> {
    override val cardinality: Int get() = 1
    override val overElements: Iterable<T> by lazy { listOf(value) }
    override fun contains(it: T): Boolean = it == value
    override fun singletonOrNull(): T = value
    override fun getPicker(random: Random) = MathematicalCollection.Picker<T> { value }
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Boolean? =
        that is Set.Finite<*> && that.singletonOrNull() == value
}