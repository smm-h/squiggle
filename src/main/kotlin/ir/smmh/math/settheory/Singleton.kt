package ir.smmh.math.settheory

import ir.smmh.math.MathematicalCollection
import ir.smmh.math.MathematicalObject
import ir.smmh.math.logic.Knowable
import ir.smmh.math.logic.Logical
import kotlin.random.Random

interface Singleton<T : MathematicalObject> : Set.Finite<T> {
    val value: T
    override val cardinality: Int get() = 1
    override val overElements: Iterable<T> get() = listOf(value)
    override fun contains(it: T) = Logical.of(it == value)
    override fun singletonOrNull(): T = value
    override fun getPicker(random: Random) = MathematicalCollection.Picker<T> { value }
    override fun isNonReferentiallyEqualTo(that: MathematicalObject): Knowable =
        if (that is Set.Finite<*>) that.singletonOrNull()?.isEqualTo(value) ?: Knowable.Unknown else Knowable.Unknown

    private class Impl<T : MathematicalObject>(override val value: T) : AbstractSet<T>(), Singleton<T> {
        override val overElements: Iterable<T> by lazy { listOf(value) }
    }

    companion object {
        fun <T : MathematicalObject> of(value: T): Singleton<T> = Impl(value)
    }
}